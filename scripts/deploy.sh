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
aws ecr get-login-password --region "${AWS_REGION}" \
  | docker login --username AWS --password-stdin "${ECR_REGISTRY}"

# 2) Change to app directory
cd "$APP_DIR"

echo "🔍 Detecting active instance"
if docker ps -q -f name=backend-blue | grep -q .; then
  CURRENT=backend-blue
  TARGET=backend-green
  PORT=8081
else
  CURRENT=backend-green
  TARGET=backend-blue
  PORT=8080
fi

echo "⏳ Switching traffic: $CURRENT → $TARGET on port $PORT"

# 3) Write .env for docker-compose
echo "📝 Writing .env for docker-compose"
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

# 4) Pull new image
echo "🚀 Pulling image ${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}"
docker pull "${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}"

# 5) Stop and remove current container if exists
echo "🛑 Stopping and removing $CURRENT if running"
if docker ps -q -f name="$CURRENT" | grep -q .; then
  docker-compose -f docker-compose-prod.yml stop "$CURRENT"
  docker-compose -f docker-compose-prod.yml rm -f "$CURRENT"
fi

# 6) Start target container
echo "▶️ Starting $TARGET"
docker-compose -f docker-compose-prod.yml --env-file .env up -d --no-deps "$TARGET"

# 7) Ensure nginx-proxy is running before updating its config
echo "⏳ Waiting for nginx-proxy to be healthy"
until docker ps --filter "name=nginx-proxy" --filter "status=running" | grep -q "nginx-proxy"; do
  echo "Waiting for nginx-proxy..."
  sleep 2
done

echo "🔁 Updating proxy_pass in default.conf"
docker exec nginx-proxy sh -c \
  "sed -i 's|proxy_pass http://[^;]*;|proxy_pass http://${TARGET}:${PORT};|' /etc/nginx/conf.d/default.conf"

# 8) Reload Nginx
echo "🔄 Reloading Nginx"
docker exec nginx-proxy nginx -s reload

echo "✅ Deployed $TARGET on port $PORT and reloaded Nginx successfully"
