Get-Content -Raw .\accounts-microservice.yaml | consul kv put config/accounts-microservice/data -
Get-Content -Raw .\api-gateway.yaml | consul kv put config/api-gateway/data -
Get-Content -Raw .\blocker-microservice.yaml | consul kv put config/blocker-microservice/data -
Get-Content -Raw .\cash-microservice.yaml | consul kv put config/cash-microservice/data -
Get-Content -Raw .\exchange-generator-microservice.yaml | consul kv put config/exchange-generator-microservice/data -
Get-Content -Raw .\exchange-microservice.yaml | consul kv put config/exchange-microservice/data -
Get-Content -Raw .\front-ui-microservice.yaml | consul kv put config/front-ui-microservice/data -
Get-Content -Raw .\notifications-microservice.yaml | consul kv put config/notifications-microservice/data -
Get-Content -Raw .\transfer-microservice.yaml | consul kv put config/transfer-microservice/data -


