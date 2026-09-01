#!/usr/bin/env sh
set -eu

GRADLE_VERSION="8.1.1"
ROOT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
BOOT_DIR="$ROOT_DIR/.gradle-bootstrap"
GRADLE_HOME_LOCAL="$BOOT_DIR/gradle-$GRADLE_VERSION"
ZIP="$BOOT_DIR/gradle-$GRADLE_VERSION-bin.zip"
URL="https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"

if [ -z "${JAVA_HOME:-}" ] || [ ! -x "${JAVA_HOME:-}/bin/javac" ]; then
  for candidate in "$HOME"/.jdks/*17* /usr/lib/jvm/*17* /Library/Java/JavaVirtualMachines/*17*/Contents/Home; do
    if [ -x "$candidate/bin/javac" ]; then
      JAVA_HOME="$candidate"
      export JAVA_HOME
      break
    fi
  done
fi

if [ -z "${JAVA_HOME:-}" ] || [ ! -x "$JAVA_HOME/bin/java" ]; then
  echo "ERROR: A JDK 17 installation is required for Forge 1.20.1 development." >&2
  exit 1
fi

PATH="$JAVA_HOME/bin:$PATH"
export PATH

if [ ! -x "$GRADLE_HOME_LOCAL/bin/gradle" ]; then
  echo "[bootstrap] Gradle $GRADLE_VERSION not found. Downloading..."
  mkdir -p "$BOOT_DIR"
  if command -v curl >/dev/null 2>&1; then
    curl -L "$URL" -o "$ZIP"
  elif command -v wget >/dev/null 2>&1; then
    wget -O "$ZIP" "$URL"
  else
    echo "curl or wget is required for the first bootstrap." >&2
    exit 1
  fi
  unzip -q -o "$ZIP" -d "$BOOT_DIR"
fi

exec "$GRADLE_HOME_LOCAL/bin/gradle" "$@"
