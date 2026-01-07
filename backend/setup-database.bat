@echo off
echo ========================================
echo    EU VAT Ease - Database Setup
echo ========================================
echo.

REM Configuration PostgreSQL
set PGPATH=C:\Program Files\PostgreSQL\18\bin
set PGHOST=localhost
set PGPORT=5432
set PGUSER=postgres
set PGPASSWORD=admin

set DB_NAME=euvatease

echo [INFO] PostgreSQL: %PGPATH%
echo [INFO] Host: %PGHOST%:%PGPORT%
echo [INFO] User: %PGUSER%
echo [INFO] Database: %DB_NAME%
echo.

REM Vérifier si la base existe déjà
echo [INFO] Verification si la base de donnees existe...
"%PGPATH%\psql.exe" -h %PGHOST% -p %PGPORT% -U %PGUSER% -lqt | findstr /C:"%DB_NAME%" > nul
if %ERRORLEVEL% EQU 0 (
    echo [WARN] La base de donnees '%DB_NAME%' existe deja.
    echo.
    set /p RECREATE="Voulez-vous la supprimer et la recreer? (o/N): "
    if /i "%RECREATE%"=="o" (
        echo [INFO] Suppression de la base de donnees...
        "%PGPATH%\psql.exe" -h %PGHOST% -p %PGPORT% -U %PGUSER% -c "DROP DATABASE IF EXISTS %DB_NAME%;"
        if %ERRORLEVEL% NEQ 0 (
            echo [ERROR] Echec de la suppression de la base de donnees.
            pause
            exit /b 1
        )
    ) else (
        echo [INFO] Operation annulee.
        pause
        exit /b 0
    )
)

REM Créer la base de données
echo [INFO] Creation de la base de donnees '%DB_NAME%'...
"%PGPATH%\psql.exe" -h %PGHOST% -p %PGPORT% -U %PGUSER% -c "CREATE DATABASE %DB_NAME% WITH ENCODING 'UTF8' LC_COLLATE 'French_France.1252' LC_CTYPE 'French_France.1252' TEMPLATE template0;"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo [SUCCESS] Base de donnees '%DB_NAME%' creee avec succes!
    echo ========================================
    echo.
    echo Vous pouvez maintenant lancer le backend avec:
    echo   start-local.bat
    echo.
    echo Flyway appliquera automatiquement les migrations
    echo au premier demarrage.
) else (
    echo.
    echo [ERROR] Echec de la creation de la base de donnees.
    echo Verifiez que PostgreSQL est bien demarre.
)
