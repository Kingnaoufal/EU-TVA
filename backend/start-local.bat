@echo off
echo ========================================
echo    EU VAT Ease - Backend Local Start
echo ========================================
echo.

REM Configuration du JDK
set JAVA_HOME=d:\Java\jdk-21.0.6
set PATH=%JAVA_HOME%\bin;%PATH%

echo [INFO] JAVA_HOME: %JAVA_HOME%
java -version
echo.

REM Configuration de la base de données locale
set SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/euvatease
set SPRING_DATASOURCE_USERNAME=postgres
set SPRING_DATASOURCE_PASSWORD=admin

REM Configuration Spring Boot
set SPRING_PROFILES_ACTIVE=local

REM JWT Secret (pour dev local uniquement)
set JWT_SECRET=local-dev-secret-key-minimum-64-characters-for-jwt-signing-algorithm

REM Shopify (à configurer si besoin)
set SHOPIFY_API_KEY=your-shopify-api-key
set SHOPIFY_API_SECRET=your-shopify-api-secret
set SHOPIFY_SCOPES=read_orders,write_orders,read_customers
set APP_URL=http://localhost:8080

REM Désactiver l'envoi d'emails en local
set SPRING_MAIL_HOST=localhost
set SPRING_MAIL_PORT=1025

echo [INFO] Database URL: %SPRING_DATASOURCE_URL%
echo [INFO] Database User: %SPRING_DATASOURCE_USERNAME%
echo [INFO] Profile: %SPRING_PROFILES_ACTIVE%
echo.
echo [INFO] Demarrage du backend Spring Boot...
echo ========================================
echo.

REM Lancement avec Maven Wrapper
call mvnw.cmd clean install
call mvnw.cmd spring-boot:run
