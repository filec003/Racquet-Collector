#!/bin/sh
set -e

# Defaults for Compose; can be overridden via environment
DB_HOSTNAME="${DB_HOSTNAME:-db}"
DB_PORT="${DB_PORT:-5432}"

echo "Waiting for ${DB_HOSTNAME}:${DB_PORT} ..."
# -z: just probe; -w 1: 1s timeout; quotes protect spaces/vars
until nc -z -w 1 "$DB_HOSTNAME" "$DB_PORT"; do
  sleep 0.2
done
echo "PostgreSQL is up ✅"

# Safe to run every start; no-op if already applied
python manage.py migrate --noinput

# Hand off to whatever CMD/args were provided
exec "$@"
