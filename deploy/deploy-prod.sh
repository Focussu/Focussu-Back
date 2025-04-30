#!/usr/bin/env bash
set -euo pipefail

# 1) 환경변수 로드
echo "🔧 Deploying with image: $BACKEND_IMAGE"

# 2) ECS 서비스 업데이트
aws ecs update-service \
  --cluster focussu-backend \
  --service focussu-backend-prod-service \
  --force-new-deployment \
  --region "$AWS_DEFAULT_REGION" \
  --output json
