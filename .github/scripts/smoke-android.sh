#!/usr/bin/env bash
# Instala cada app (release, con R8) en el emulador, la abre y espera a que
# aparezca "Rick Sanchez" en pantalla: prueba arranque, DI, red, Room y R8.
set -uo pipefail
mkdir -p smoke
status=0

# El emulador termina de arrancar antes de tener internet; esperar a que la API responda.
for _ in $(seq 1 60); do
  adb shell ping -c 1 -W 2 rickandmortyapi.com >/dev/null 2>&1 && break
  sleep 2
done

check() {
  local pkg=$1 apk=$2
  adb install -r "$apk" >/dev/null
  adb logcat -c
  adb shell monkey -p "$pkg" -c android.intent.category.LAUNCHER 1 >/dev/null 2>&1
  for _ in $(seq 1 30); do
    sleep 2
    adb shell uiautomator dump /sdcard/ui.xml >/dev/null 2>&1 || true
    if adb shell cat /sdcard/ui.xml 2>/dev/null | grep -q "Rick Sanchez"; then
      adb exec-out screencap -p > "smoke/$pkg.png"
      echo "OK   $pkg"
      adb shell am force-stop "$pkg"
      return 0
    fi
  done
  adb exec-out screencap -p > "smoke/$pkg.png"
  echo "FAIL $pkg"
  adb logcat -d | grep -A40 "FATAL EXCEPTION" || adb logcat -d | tail -80
  status=1
}

check com.jcjiron.androidsample     app/build/outputs/apk/release/app-release.apk
check com.jcjiron.androidsample.mvi app-mvi/build/outputs/apk/release/app-mvi-release.apk
check com.jcjiron.androidsample.mvp app-mvp/build/outputs/apk/release/app-mvp-release.apk
check com.jcjiron.androidsample.kmp app-kmp/build/outputs/apk/release/app-kmp-release.apk
exit $status
