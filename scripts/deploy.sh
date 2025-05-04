#!/usr/bin/env bash
set -euo pipefail

# Load environment variables from .env in app root
ENV_FILE="/home/ubuntu/app/.env"
if [ -f "$ENV_FILE" ]; then
  set -a
  # shellcheck disable=SC1090
  source "$ENV_FILE"
  set +a
else
  echo "Error: .env file not found at $ENV_FILE" >&2
  exit 1
fi

APP_DIR="/home/ubuntu/app"

echo "🔧 Deploy parameters from .env:"
echo "  AWS_REGION    = ${AWS_REGION}"
echo "  ECR_REGISTRY  = ${ECR_REGISTRY}"
echo "  ECR_REPOSITORY= ${ECR_REPOSITORY}"
echo "  IMAGE_TAG     = ${IMAGE_TAG}"
echo

# 1) AWS ECR login
echo "🔑 Logging into ECR"
aws ecr get-login-password --region "${AWS_REGION}" \
  | docker login --username AWS --password-stdin "${ECR_REGISTRY}"

# 2) Change to app directory
echo "📁 Switching to $APP_DIR"
cd "$APP_DIR"

# 3) Detect active instance and set target for blue-green
echo "🔍 Detecting active container"
if docker ps -q -f name=backend-blue | grep -q .; then
  CURRENT=backend-blue
  TARGET=backend-green
  PORT=8081
else
  CURRENT=backend-green
  TARGET=backend-blue
  PORT=8080
fi

echo "⏳ Swapping traffic: $CURRENT → $TARGET on port $PORT"

# 4) Write .env for docker-compose
echo "📝 Generating .env file for docker-compose"
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

# 5) Pull new image
echo "🚀 Pulling image ${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}"
docker pull "${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}"

# 6) Stop and remove current container if exists
echo "🛑 Stopping and removing $CURRENT if running"
if docker ps -q -f name="$CURRENT" | grep -q .; then
  docker-compose -f docker-compose-prod.yml stop "$CURRENT"
  docker-compose -f docker-compose-prod.yml rm -f "$CURRENT"
fi

# 7) Start target container
echo "▶️ Starting $TARGET"
docker-compose -f docker-compose-prod.yml --env-file .env up -d --no-deps "$TARGET"

# 8) Restart nginx-proxy to pickup new backend
echo "🔄 Restarting nginx-proxy service"
docker-compose -f docker-compose-prod.yml up -d nginx

echo "✅ Deployed $TARGET on port $PORT and restarted nginx-proxy successfully"
