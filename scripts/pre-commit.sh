#!/usr/bin/env bash

# Fail if any commands fails.
set -e

# Check dependencies
if ! command -v kotlinc > /dev/null 2>&1; then
    echo "Please install kotlin with: brew install kotlin"
    exit 1
fi
if ! command -v ktfmt > /dev/null 2>&1; then
    echo "Please install ktfmt with: brew install ktfmt"
    exit 1
fi

UNSTAGED=$(git diff --diff-filter=ACMRT --name-only | sort)
STAGED=$(git diff --cached --diff-filter=ACMRT --name-only  | sort)
FULLY_STAGED=$(comm -23 <(echo "$STAGED") <(echo "$UNSTAGED"))
PARTIALLY_STAGED=$(comm -12 <(echo "$STAGED") <(echo "$UNSTAGED"))

# Staged files that have no further modifications are safe and easy to reformat.
if [[ -n "$FULLY_STAGED" ]]; then
  echo "$FULLY_STAGED" | xargs scripts/check-code-formatting.sh --fix
  echo "$FULLY_STAGED" | xargs git add --
fi

# Partially staged files need extra precautions.
if [[ -n "$PARTIALLY_STAGED" ]]; then
  # Temporarily hide the unstaged changes.
  git stash push --keep-index --message "before pre-commit code formatting check" --quiet
  # Check formatting.
  NOT_WELL_FORMATTED=$(echo "$STAGED" | xargs scripts/check-code-formatting.sh) \
    || true # Don't exit this script if the check fails
  # Restore unstaged modifications.
  git reset --hard --quiet
  git stash pop --index --quiet
fi

# If some partially staged files aren't well formatted..
if [[ -n "$NOT_WELL_FORMATTED" ]]; then
  # ..format them to make it easier for the developer..
  echo "$NOT_WELL_FORMATTED" | xargs scripts/check-code-formatting.sh --fix
  # ..but don't try to stage them, leave that to the developer.
  echo
  echo "ERROR: Reformatted following files, but couldn't stage modifications safely:"
  echo "$NOT_WELL_FORMATTED"
  echo "Stage modifications manually and commit again."
  exit 1
fi
