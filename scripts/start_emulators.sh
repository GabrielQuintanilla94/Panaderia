#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
# demo- prefix prevents accidental operations against a real Firebase project.
exec npx --yes firebase-tools@14.22.0 emulators:start --only auth,firestore --project demo-panaderia
