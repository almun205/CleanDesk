@echo off
SETLOCAL ENABLEEXTENSIONS

set "PROJECT_DIR=%~dp0.."
if not exist "%PROJECT_DIR%\.env" (
  echo Fehler: .env fehlt. Kopiere .env.example nach .env und trage das Datenbankpasswort ein.
  exit /b 1
)
for /f "usebackq eol=# tokens=1,* delims==" %%A in ("%PROJECT_DIR%\.env") do set "%%A=%%B"

where mvn >NUL 2>&1
IF ERRORLEVEL 1 (
  echo Fehler: Maven ist nicht installiert oder nicht im PATH.
  exit /b 1
)

pushd "%PROJECT_DIR%"
echo ==> Starte CleanDesk ...
mvn -q -DskipTests compile exec:java
set "EXIT_CODE=%ERRORLEVEL%"
popd
if not "%EXIT_CODE%"=="0" exit /b %EXIT_CODE%
ENDLOCAL
