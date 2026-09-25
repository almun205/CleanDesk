#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd -- "${SCRIPT_DIR}/.." && pwd)"

if [[ -f "${PROJECT_DIR}/.env" ]]; then
  set -a
  # shellcheck disable=SC1091
  source "${PROJECT_DIR}/.env"
  set +a
else
  echo "Fehler: .env fehlt. Kopiere .env.example nach .env und trage das Datenbankpasswort ein."
  exit 1
fi

echo "==> CleanDesk: Setup startet"

# Optional: einfacher Java-Version-Check (nur Hinweis)
if command -v java >/dev/null 2>&1; then
  JAVA_VER="$(java -version 2>&1 | head -n1)"
  echo "Gefundene Java-Version: ${JAVA_VER}"
  if ! java -version 2>&1 | grep -q 'version "23'; then
    echo "Hinweis: Empfohlen ist Java 23. Fortfahren wird dennoch versucht."
  fi
else
  echo "Fehler: Java ist nicht installiert oder nicht im PATH."
  exit 1
fi

if ! command -v mvn >/dev/null 2>&1; then
  echo "Fehler: Maven ist nicht installiert oder nicht im PATH."
  exit 1
fi

cd "${PROJECT_DIR}"
echo "==> Projekt bauen (Tests ausführen)"
mvn -B verify

echo "==> Fertig. Du kannst die App nun mit ./scripts/run.sh starten."
