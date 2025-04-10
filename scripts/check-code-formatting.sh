#!/usr/bin/env bash

# Fail if any commands fails.
set -e

if [[ $1 == "--fix" ]]; then
  AUTO_FIX=true
  shift
else
  AUTO_FIX=false
fi

if [[ $# -gt 0 ]]; then
  # Capture arguments in an array.
  ARGS=( "$@" )
else
  # Default to the root project directory if none passed.
  ARGS=(".")
fi

DIRS=()
for ARG in "${ARGS[@]}"; do
  if [[ -d "$ARG" ]]; then
    DIRS+=("$ARG")
  fi
done

KOTLIN_FILES=()
for ARG in "${ARGS[@]}"; do
  if [[ "$ARG" == *".kt" || "$ARG" == *".kts" ]]; then
    KOTLIN_FILES+=("$ARG")
  fi
done

# ktfmt expects either a list of .kt files, or a name of a directory
# whose contents the user wants to format
# See: https://github.com/facebook/ktfmt/blob/bcec853/core/src/main/java/com/facebook/ktfmt/cli/Main.kt#L60-L66
if [[ "${#DIRS[@]}" -gt 0 || "${#KOTLIN_FILES[@]}" -gt 0 ]]; then
  # Check dependencies
  if ! command -v ktfmt > /dev/null 2>&1; then
      echo "Please install ktfmt with: brew install ktfmt"
      exit 1
  fi

  KTFMT_FLAGS=("--kotlinlang-style")
  if [[ "$AUTO_FIX" == "false" ]]; then
    KTFMT_FLAGS+=("--dry-run" "--set-exit-if-changed")
  fi
  ktfmt "${KTFMT_FLAGS[@]}" "${DIRS[@]}" "${KOTLIN_FILES[@]}"
fi

# Check if one of the args is either the version catalog or a directory that contains it.
VERSION_CATALOG_PATH="./gradle/libs.versions.toml"
SORT_VERSION_CATALOG=false
for ARG in "${ARGS[@]}"; do
  if [[ "$(realpath $VERSION_CATALOG_PATH)" == "$(realpath "$ARG")"* ]]; then
    SORT_VERSION_CATALOG=true
  fi
done

if [[ "$SORT_VERSION_CATALOG" == "true" ]]; then
  # Check dependencies
  if ! command -v kotlin > /dev/null 2>&1; then
      echo "Please install kotlin with: brew install kotlin"
      exit 1
  fi

  if [[ "$AUTO_FIX" == "false" ]]; then
    VERSION_CATALOG_FLAGS=("--dry-run")
  else
    VERSION_CATALOG_FLAGS=("--fix")
  fi
  scripts/sort-version-catalog.main.kts "${VERSION_CATALOG_FLAGS[@]}" "$VERSION_CATALOG_PATH"
fi
