#!/usr/bin/env bash

# Fail if any commands fails.
set -e

# Check dependencies
if ! command -v ktfmt > /dev/null 2>&1; then
    echo "Please install ktfmt with: brew install ktfmt"
    exit 1
fi

if [ $# -gt 0 ]; then
  # Capture arguments in an array.
  ARGS=( "$@" )
else
  # Default to the root project directory if none passed.
  ARGS=(".")
fi

DIRS=()
for ARG in "${ARGS[@]}"; do
  :
  if [[ -d "$ARG" ]]; then
    DIRS+=("$ARG")
  fi
done

KOTLIN_FILES=()
for ARG in "${ARGS[@]}"; do
  :
  if [[ "$ARG" == "*.kt" || "$ARG" == "*.kts" ]]; then
    KOTLIN_FILES+=("$ARG")
  fi
done

if [[ "${#DIRS[@]}" -gt 0 || "${#KOTLIN_FILES[@]}" -gt 0 ]]; then
  ktfmt --kotlinlang-style "${DIRS[@]}" "${KOTLIN_FILES[@]}"
fi
