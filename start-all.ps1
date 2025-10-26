# 1. Создание Docker-сети
docker network create mynet

# 2. Запуск контейнера Consul
docker run -d -p 8500:8500 --network mynet --name consul `
    consul:1.15.4 agent -dev -client "0.0.0.0"

Start-Sleep -Seconds 5

# 3. Запуск контейнера Keycloak
Write-Host "Starting Keycloak container..."
$CurrentPath = Get-Location
docker run -d `
    -v "${CurrentPath}/realm-export.json:/opt/keycloak/data/import/realm-export.json" `
    -p 8090:8080 --network mynet `
    --name keycloak `
    -e TZ=Europe/Samara `
    -e KC_BOOTSTRAP_ADMIN_USERNAME=admin `
    -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin `
    quay.io/keycloak/keycloak:26.1.3 start-dev --import-realm

Start-Sleep -Seconds 5

# 4. Запуск скрипта сохранения конфигов
Push-Location consul
Write-Host "Running save_configs_via_api.ps1..."
& "./save_configs_via_api.ps1"
Pop-Location

# 5. Список папок для запуска
$folders = @(
    "api-gateway",
    "eureka",
    "notifications-microservice",
    "blocker-microservice",
    "accounts-microservice",
    "exchange-microservice",
    "transfer-microservice",
    "cash-microservice",
    "exchange-generator-microservice",
    "front-ui-microservice"
)

# 6. Проход по всем папкам
foreach ($folder in $folders) {
    Write-Host "`nProcessing $folder..."
    Push-Location $folder
    .\run.ps1
    Pop-Location
}
