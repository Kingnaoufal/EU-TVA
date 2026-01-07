# Script de démarrage du backend EU VAT Ease
$env:JAVA_HOME = "d:\Java\jdk-21.0.6"
$env:DATABASE_PASSWORD = "admin"
$env:DATABASE_USERNAME = "postgres"
$env:DATABASE_URL = "jdbc:postgresql://localhost:5432/euvatease"

Set-Location "d:\naoufal\eu-tva\backend"

Write-Host "Démarrage du backend EU VAT Ease..." -ForegroundColor Green
Write-Host "JAVA_HOME: $env:JAVA_HOME" -ForegroundColor Cyan
Write-Host "DATABASE_URL: $env:DATABASE_URL" -ForegroundColor Cyan

& .\mvnw.cmd spring-boot:run
