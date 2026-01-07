@echo off
echo Starting EU VAT Ease Backend...
set JAVA_HOME=d:\Java\jdk-21.0.6
set DATABASE_PASSWORD=admin
cd /d %~dp0
call mvnw.cmd spring-boot:run
pause
