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

# ktfmt expects either a list of .kt files, or a name of a directory
# whose contents the user wants to format.
# See: https://github.com/facebook/ktfmt/blob/bcec853/core/src/main/java/com/facebook/ktfmt/cli/Main.kt#L60-L66
if [[ "${#DIRS[@]}" -gt 0 || "${#KOTLIN_FILES[@]}" -gt 0 ]]; then
  ktfmt --kotlinlang-style --dry-run --set-exit-if-changed "${DIRS[@]}" "${KOTLIN_FILES[@]}"
fi
