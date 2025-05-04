#!/usr/bin/env bash
set -euo pipefail

APP_DIR=/home/ubuntu/app

# 1) AWS 로그인
aws ecr get-login-password --region "${AWS_REGION}" \
  | docker login --username AWS --password-stdin "${ECR_REGISTRY}"

cd "${APP_DIR}"

# 2) 안전한 서비스 감지 (case 방식)
BLUE_ID="$(docker ps -q -f name=backend-blue || true)"
case "${BLUE_ID}" in
  "") 
    CURRENT=backend-green
    TARGET=backend-blue
    PORT=8080
    ;;
  *)  
    CURRENT=backend-blue
    TARGET=backend-green
    PORT=8081
    ;;
esac
echo "⏳ Switching from ${CURRENT} to ${TARGET} …"

# 3) .env 파일 생성
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

# 5) 기존 컨테이너 중지·제거 (존재 시)
if [ -n "$(docker ps -q -f name=${CURRENT})" ]; then
  docker-compose -f docker-compose-prod.yml stop "${CURRENT}"
  docker-compose -f docker-compose-prod.yml rm -f "${CURRENT}"
fi

# 6) 새 컨테이너 배포
docker-compose -f docker-compose-prod.yml --env-file .env up -d --no-deps "${TARGET}"

# 7) Nginx 트래픽 스위칭
echo "set \$service_url http://${TARGET}:${PORT};" > service-env.inc
if [ -z "$(docker ps -q -f name=nginx-proxy)" ]; then
  docker-compose -f docker-compose-prod.yml up -d nginx
fi
docker exec nginx-proxy nginx -s reload

echo "✅ Deployed ${TARGET} (image:${IMAGE_TAG}); stopped ${CURRENT}."
