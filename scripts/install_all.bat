@echo off
SETLOCAL ENABLEEXTENSIONS

set "PROJECT_DIR=%~dp0.."
if not exist "%PROJECT_DIR%\.env" (
  echo Fehler: .env fehlt. Kopiere .env.example nach .env und trage das Datenbankpasswort ein.
  exit /b 1
)
for /f "usebackq eol=# tokens=1,* delims==" %%A in ("%PROJECT_DIR%\.env") do set "%%A=%%B"

echo ==> CleanDesk: Setup startet

where java >NUL 2>&1
IF ERRORLEVEL 1 (
  echo Fehler: Java ist nicht installiert oder nicht im PATH.
  exit /b 1
)

for /f "tokens=2 delims==" %%v in ('java -XshowSettings:properties -version 2^>^&1 ^| findstr "java.version"') do set JAVAVER=%%v
echo Gefundene Java-Version:%JAVAVER%

where mvn >NUL 2>&1
IF ERRORLEVEL 1 (
  echo Fehler: Maven ist nicht installiert oder nicht im PATH.
  exit /b 1
)

pushd "%PROJECT_DIR%"
echo ==> Projekt bauen (Tests ausführen)
mvn -B verify
IF ERRORLEVEL 1 (
  popd
  exit /b 1
)

echo ==> Fertig. Du kannst die App nun mit scripts\run.bat starten.
popd
ENDLOCAL
