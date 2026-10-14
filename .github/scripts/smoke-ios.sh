#!/usr/bin/env bash
# Instala iosApp en un simulador de iPhone, la abre y revisa que siga viva a los 30 s.
set -euo pipefail
APP=iosApp/build/Build/Products/Debug-iphonesimulator/iosApp.app
BUNDLE=com.jcjiron.androidsample.kmp
UDID=$(xcrun simctl list devices available -j | python3 -c '
import json, sys
devices = json.load(sys.stdin)["devices"]
print(next(d["udid"] for runtime, ds in devices.items() if "iOS" in runtime for d in ds if d["name"].startswith("iPhone")))
')
xcrun simctl boot "$UDID"
xcrun simctl bootstatus "$UDID" -b
xcrun simctl install "$UDID" "$APP"
xcrun simctl launch "$UDID" "$BUNDLE"
sleep 30
mkdir -p smoke
xcrun simctl io "$UDID" screenshot smoke/ios.png
if xcrun simctl spawn "$UDID" launchctl list | grep -q "$BUNDLE"; then
  echo "OK   iOS: la app sigue corriendo"
else
  echo "FAIL iOS: la app se cerró"
  xcrun simctl spawn "$UDID" log show --last 2m --predicate "process == 'iosApp'" | tail -80 || true
  exit 1
fi
