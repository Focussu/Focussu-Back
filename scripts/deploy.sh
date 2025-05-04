#!/usr/bin/env bash
set -euo pipefail

if [ $# -ne 4 ]; then
  echo "Usage: $0 <AWS_REGION> <ECR_REGISTRY> <ECR_REPOSITORY> <IMAGE_TAG>"
  exit 1
fi

AWS_REGION="$1"
ECR_REGISTRY="$2"
ECR_REPOSITORY="$3"
IMAGE_TAG="$4"

APP_DIR=/home/ubuntu/app

echo "🔧 Deploy parameters:"
echo "  AWS_REGION   = ${AWS_REGION}"
echo "  ECR_REGISTRY = ${ECR_REGISTRY}"
echo "  ECR_REPO     = ${ECR_REPOSITORY}"
echo "  IMAGE_TAG    = ${IMAGE_TAG}"
echo

# 1) AWS ECR 로그인
aws ecr get-login-password --region "${AWS_REGION}" \
  | docker login --username AWS --password-stdin "${ECR_REGISTRY}"

cd "${APP_DIR}"

# 2) 현재 살아있는 블루 인스턴스 감지
BLUE_ID="$(docker ps -q -f name=backend-blue || true)"
if [ -z "$BLUE_ID" ]; then
  CURRENT=backend-green
  TARGET=backend-blue
  PORT=8080
else
  CURRENT=backend-blue
  TARGET=backend-green
  PORT=8081
fi
echo "⏳ Switching from ${CURRENT} → ${TARGET} (port ${PORT}) …"

# 3) .env 파일 생성 (docker-compose용)
cat > .env <<EOF
RDS_ENDPOINT=${RDS_ENDPOINT}
RDS_PORT=${RDS_PORT}
RDS_DATABASE=${RDS_DATABASE}
RDS_USERNAME=${RDS_USERNAME}
RDS_PASSWORD=${RDS_PASSWORD}
ELASTICACHE_ENDPOINT=${ELASTICACHE_ENDPOINT}
ELASTICACHE_PORT=${ELASTICACHE_PORT}
JWT_SECRET_KEY=${JWT_SECRET_KEY}
JWT_EXPIRATION_TIME=${JWT_EXPIRATION_TIME}
ECR_REGISTRY=${ECR_REGISTRY}
ECR_REPOSITORY=${ECR_REPOSITORY}
IMAGE_TAG=${IMAGE_TAG}
EOF

# 4) 새 이미지 pull
docker pull "${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}"

# 5) 기존 컨테이너 중지·제거
if docker ps -q -f name="${CURRENT}" | grep -q .; then
  docker-compose -f docker-compose-prod.yml stop "${CURRENT}"
  docker-compose -f docker-compose-prod.yml rm -f "${CURRENT}"
fi

# 6) 새 컨테이너 기동
docker-compose -f docker-compose-prod.yml --env-file .env up -d --no-deps "${TARGET}"

# 7) Nginx 트래픽 스위칭
echo "set \$service_url http://${TARGET}:${PORT};" > service-env.inc

if ! docker ps -q -f name=nginx-proxy | grep -q .; then
  docker-compose -f docker-compose-prod.yml up -d nginx
fi
docker exec nginx-proxy nginx -s reload

echo "✅ Deployed ${TARGET} (image:${IMAGE_TAG}); stopped ${CURRENT}."
