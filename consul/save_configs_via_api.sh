#!/bin/bash
set -e

CONSUL_URL="http://localhost:8500/v1/kv"

services=(
  "accounts-microservice"
  "blocker-microservice"
  "cash-microservice"
  "exchange-generator-microservice"
  "exchange-microservice"
  "front-ui-microservice"
  "notifications-microservice"
  "transfer-microservice"
)

for service in "${services[@]}"; do
  file="./${service}.yaml"
  key="config/${service}/data"

  if [ -f "$file" ]; then
    echo "Uploading $file to Consul at key $key..."

    curl -s -X PUT --data-binary @"$file" "${CONSUL_URL}/${key}" > /dev/null

    echo "Done"
  else
    echo "  $file not found, skipping..."
  fi
done
