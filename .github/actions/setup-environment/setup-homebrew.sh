#!/usr/bin/env bash

# Fail if any commands fails.
set -e

echo "Enabling Homebrew for subsequent actions…"
# https://github.com/actions/runner-images/blob/main/images/ubuntu/Ubuntu2404-Readme.md#homebrew-note
HOMEBREW_PREFIX="/home/linuxbrew/.linuxbrew"
# https://docs.github.com/en/actions/using-workflows/workflow-commands-for-github-actions#adding-a-system-path
echo "$HOMEBREW_PREFIX/sbin" >>"$GITHUB_PATH"
echo "$HOMEBREW_PREFIX/bin" >>"$GITHUB_PATH"
