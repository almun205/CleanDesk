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

if ! command -v mvn >/dev/null 2>&1; then
  echo "Fehler: Maven ist nicht installiert oder nicht im PATH."
  exit 1
fi

cd "${PROJECT_DIR}"
echo "==> Starte CleanDesk ..."
mvn -q -DskipTests compile exec:java
