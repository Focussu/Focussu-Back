#!/usr/bin/env bash
set -euo pipefail

# 0) 작업 디렉터리
APP_DIR="/home/ubuntu/app"
cd "$APP_DIR"

# 1) .env 로드 (ECR, DB, Redis 등)
ENV_FILE="$APP_DIR/.env"
if [ -f "$ENV_FILE" ]; then
  set -a && source "$ENV_FILE" && set +a
else
  echo "Error: .env 파일이 없습니다." >&2
  exit 1
fi

echo "🔧 Deploy parameters:"
echo "  AWS_REGION    = ${AWS_REGION}"
echo "  ECR_REGISTRY  = ${ECR_REGISTRY}"
echo "  ECR_REPOSITORY= ${ECR_REPOSITORY}"
echo "  IMAGE_TAG     = ${IMAGE_TAG}"
echo

# 2) ECR 로그인
echo "🔑 AWS ECR 로그인"
aws ecr get-login-password --region "${AWS_REGION}" \
  | docker login --username AWS --password-stdin "${ECR_REGISTRY}"

# 3) 현재 살아있는 컨테이너 감지 및 TARGET 설정
echo "🔍 활성 컨테이너 감지"
if docker ps -q -f name=backend-blue | grep -q .; then
  CURRENT=backend-blue; TARGET=backend-green; HOST_PORT=8081
else
  CURRENT=backend-green; TARGET=backend-blue; HOST_PORT=8080
fi
echo "⏳ 스위칭: ${CURRENT} → ${TARGET} (호스트 포트 ${HOST_PORT})"

# 4) service-env.inc 동적 생성 (Nginx proxy 대상 변경)
cat > service-env.inc <<EOF
set \$service_url http://${TARGET}:8080;
EOF
echo "📝 service-env.inc → proxy to ${TARGET}:8080"

# 5) 새 이미지 풀
echo "🚀 이미지 풀링: ${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}"
docker pull "${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}"

# 6) 기존 컨테이너 중지 및 제거
echo "🛑 ${CURRENT} 중지·제거"
if docker ps -q -f name="$CURRENT" | grep -q .; then
  docker-compose -f docker-compose-prod.yml stop "$CURRENT"
  docker-compose -f docker-compose-prod.yml rm -f "$CURRENT"
fi

# 7) TARGET 컨테이너 시작
echo "▶️ ${TARGET} 시작"
docker-compose -f docker-compose-prod.yml --env-file .env up -d --no-deps "$TARGET"

# 8) Nginx 재기동 또는 설정 리로드
echo "🔄 nginx-proxy 재기동"
docker restart nginx-proxy

echo "✅ 배포 완료: ${TARGET} → ${TARGET}:8080 (호스트 ${HOST_PORT})"
