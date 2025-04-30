#!/usr/bin/env bash
set -euo pipefail

CLUSTER="focussu-backend"
SERVICE="focussu-backend-prod-service"
REGION="${AWS_DEFAULT_REGION}"

echo "🔧 Deploying image: $BACKEND_IMAGE (region: $REGION)"

# 1) 현재 Task Definition 불러오기
TD_JSON=$(aws ecs describe-task-definition \
  --task-definition "$SERVICE" \
  --region "$REGION" \
  --output json)

# 2) jq로 image & env 업데이트
NEW_DEF=$(echo "$TD_JSON" | jq -r --arg IMG "$BACKEND_IMAGE" \
  --arg SPRING_PROFILES_ACTIVE "$SPRING_PROFILES_ACTIVE" \
  --arg RDS_ENDPOINT "$RDS_ENDPOINT" \
  --arg RDS_PORT "$RDS_PORT" \
  --arg RDS_DATABASE "$RDS_DATABASE" \
  --arg RDS_USER "$RDS_USER" \
  --arg RDS_PASSWORD "$RDS_PASSWORD" \
  --arg ELASTICACHE_ENDPOINT "$ELASTICACHE_ENDPOINT" \
  --arg ELASTICACHE_PORT "$ELASTICACHE_PORT" \
  --arg JWT_SECRET_KEY "$JWT_SECRET_KEY" \
  --arg JWT_EXPIRATION_TIME "$JWT_EXPIRATION_TIME" \
  --arg KAFKA_BOOTSTRAP_SERVERS "$KAFKA_BOOTSTRAP_SERVERS" '
.taskDefinition
| {
    family: .family,
    networkMode: .networkMode,
    requiresCompatibilities: .requiresCompatibilities,
    cpu: .cpu,
    memory: .memory,
    containerDefinitions:
      (.containerDefinitions | map(
         .image = $IMG
         | .environment = [
             { name: "SPRING_PROFILES_ACTIVE",  value: $SPRING_PROFILES_ACTIVE },
             { name: "RDS_ENDPOINT",            value: $RDS_ENDPOINT },
             { name: "RDS_PORT",                value: $RDS_PORT },
             { name: "RDS_DATABASE",            value: $RDS_DATABASE },
             { name: "RDS_USER",                value: $RDS_USER },
             { name: "RDS_PASSWORD",            value: $RDS_PASSWORD },
             { name: "ELASTICACHE_ENDPOINT",    value: $ELASTICACHE_ENDPOINT },
             { name: "ELASTICACHE_PORT",        value: $ELASTICACHE_PORT },
             { name: "JWT_SECRET_KEY",          value: $JWT_SECRET_KEY },
             { name: "JWT_EXPIRATION_TIME",     value: $JWT_EXPIRATION_TIME },
             { name: "KAFKA_BOOTSTRAP_SERVERS", value: $KAFKA_BOOTSTRAP_SERVERS }
           ]
      ))
  }
')

# 3) 새 Task Definition 등록
aws ecs register-task-definition \
  --cli-input-json "$NEW_DEF" \
  --region "$REGION" > /dev/null

# 4) 서비스 롤링 업데이트
aws ecs update-service \
  --cluster "$CLUSTER" \
  --service "$SERVICE" \
  --force-new-deployment \
  --region "$REGION" \
  --output json

echo "✅ Deployment triggered for $SERVICE"
