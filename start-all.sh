#!/bin/bash
set -e  # Остановить выполнение при ошибке

# 1. Создание Docker-сети
docker network create mynet || true

# 2. Запуск контейнера Consul
echo "Starting Consul container..."
docker run -d -p 8500:8500 --network mynet --name consul \
    consul:1.15.4 agent -dev -client "0.0.0.0"

sleep 5

# 2. Запуск контейнера Keycloak
echo "Starting Keycloak container..."
docker run -d \
    -v "$(pwd)/realm-export.json:/opt/keycloak/data/import/realm-export.json" \
    -p 8090:8080 --network mynet \
    --name keycloak \
    -e TZ=Europe/Samara \
    -e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
    -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
    quay.io/keycloak/keycloak:26.1.3 start-dev --import-realm

sleep 5

# 3. Запуск скрипта сохранения конфигов
echo "Running save_configs_via_api.sh..."
pushd consul >/dev/null
./save_configs_via_api.sh
popd >/dev/null

# 4. Список папок для запуска
folders=(
    "api-gateway"
    "eureka"
    "notifications-microservice"
    "blocker-microservice"
    "accounts-microservice"
    "exchange-microservice"
    "transfer-microservice"
    "cash-microservice"
    "exchange-generator-microservice"
    "front-ui-microservice"
)

# 5. Проход по всем папкам
for folder in "${folders[@]}"; do
    echo -e "\nProcessing $folder..."
    pushd "$folder" >/dev/null
    chmod +x run.sh 2>/dev/null || true
    ./run.sh
    popd >/dev/null
done
