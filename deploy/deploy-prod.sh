#!/usr/bin/env bash
set -euo pipefail

# —————————————————————————————————
# 0) 필수 환경변수
: "${BACKEND_IMAGE:?Need to set BACKEND_IMAGE (e.g. <ECR_URI>:<TAG>)}"
: "${AWS_DEFAULT_REGION:?Need to set AWS_DEFAULT_REGION}"

# ECS 리소스 이름
ECS_CLUSTER="focussu-backend"
ECS_SERVICE="focussu-backend-prod-service"
# —————————————————————————————————

echo "🔧 Deploying image → $BACKEND_IMAGE"

# 1) 현재 서비스에 연결된 Task Definition ARN 조회
CURRENT_TD_ARN=$(aws ecs describe-services \
  --cluster "$ECS_CLUSTER" \
  --services "$ECS_SERVICE" \
  --query 'services[0].taskDefinition' \
  --output text)
echo "ℹ️ Current task definition ARN: $CURRENT_TD_ARN"

# 2) Task Definition 상세 가져오기
TD_JSON=$(aws ecs describe-task-definition \
  --task-definition "$CURRENT_TD_ARN" \
  --output json \
  --query 'taskDefinition')

# 3) register-task-definition 에 넘길 JSON로 정리
TD_REG_INPUT=$(echo "$TD_JSON" | jq \
  'del(.taskDefinitionArn, .revision, .status, .requiresAttributes, .compatibilities, .registeredAt, .registeredBy)
   | .containerDefinitions[0].image = "'"$BACKEND_IMAGE"'"'
)

# 4) 새 Task Definition 리비전 등록
NEW_TD_ARN=$(aws ecs register-task-definition \
  --cli-input-json "$TD_REG_INPUT" \
  --query 'taskDefinition.taskDefinitionArn' \
  --output text)
echo "🆕 Registered new task definition ARN: $NEW_TD_ARN"

# 5) 서비스 업데이트 + 강제 재배포
aws ecs update-service \
  --cluster "$ECS_CLUSTER" \
  --service "$ECS_SERVICE" \
  --task-definition "$NEW_TD_ARN" \
  --force-new-deployment \
  --region "$AWS_DEFAULT_REGION" \
  --output json

echo "✅ Deployment triggered!"
