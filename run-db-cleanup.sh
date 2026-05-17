#!/bin/bash

# Run the Zer roo database cleanup script with the configured credentials.
# Review db-cleanup.sql before executing.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SQL_FILE="$SCRIPT_DIR/db-cleanup.sql"

if [[ ! -f "$SQL_FILE" ]]; then
  echo "ERROR: cleanup SQL file not found: $SQL_FILE"
  exit 1
fi

read -p "This will truncate business data tables in the zerroo database. Continue? [y/N] " confirm
if [[ "$confirm" != "y" && "$confirm" != "Y" ]]; then
  echo "Aborted."
  exit 0
fi

PGPASSWORD='Zerroo@2024!Secure' psql -h localhost -U zerroo_user -d zerroo -f "$SQL_FILE"

echo "Database cleanup complete."
