#!/bin/sh
set -eu
project_dir=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
wrapper_jar="$project_dir/gradle/wrapper/gradle-wrapper.jar"
expected_hash='498495120a03b9a6ab5d155f5de3c8f0d986a449153702fb80fc80e134484f17'
checksum() {
    if command -v sha256sum >/dev/null 2>&1; then sha256sum "$1" | awk '{print $1}'
    else shasum -a 256 "$1" | awk '{print $1}'; fi
}
if [ -f "$wrapper_jar" ]; then
    [ "$(checksum "$wrapper_jar")" = "$expected_hash" ] || { echo 'Checksum wrapper tidak cocok.' >&2; exit 1; }
    echo 'Gradle Wrapper sudah siap.'
    exit 0
fi
download_target="$wrapper_jar.download"
trap 'rm -f "$download_target"' EXIT HUP INT TERM
curl -fL --connect-timeout 30 --max-time 180 'https://raw.githubusercontent.com/gradle/gradle/v8.9.0/gradle/wrapper/gradle-wrapper.jar' -o "$download_target"
[ "$(checksum "$download_target")" = "$expected_hash" ] || { echo 'Checksum unduhan tidak cocok.' >&2; exit 1; }
mv "$download_target" "$wrapper_jar"
echo 'Siap. Buka folder Calculator melalui Android Studio > Open.'
