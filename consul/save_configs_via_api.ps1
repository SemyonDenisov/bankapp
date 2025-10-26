$consulUrl = "http://localhost:8500/v1/kv"

$services = @(
  "accounts-microservice",
  "blocker-microservice",
  "cash-microservice",
  "exchange-generator-microservice",
  "exchange-microservice",
  "front-ui-microservice",
  "notifications-microservice",
  "transfer-microservice"
)

foreach ($service in $services) {
    $file = ".\$service.yaml"
    $key = "config/$service/data"
    
    if (Test-Path $file) {
        Write-Host "Uploading $file to Consul at key $key..."

        $body = Get-Content $file -Raw
        $uri = "$consulUrl/$key"

        
        Invoke-RestMethod -Method Put -Uri $uri -Body $body

        Write-Host "Done"
    } else {
        Write-Warning "$file not found, skipping..."
    }
}
