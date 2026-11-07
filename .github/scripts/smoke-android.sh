#!/usr/bin/env bash
# Smoke test en emulador con los builds release (R8). Prueba arranque, red, Room y
# los 4 componentes de Android en :app. Las capturas quedan en smoke/.
set -uo pipefail
mkdir -p smoke
status=0
APP=com.jcjiron.androidsample

dump() { adb shell uiautomator dump /sdcard/ui.xml >/dev/null 2>&1; adb shell cat /sdcard/ui.xml 2>/dev/null; }

# Espera hasta $2 intentos (cada 2 s) a que el texto $1 aparezca en pantalla.
wait_text() {
  for _ in $(seq 1 "${2:-30}"); do
    dump | grep -q "$1" && return 0
    sleep 2
  done
  return 1
}

# Toca el centro del elemento cuyo texto es $1.
tap_text() {
  local b
  b=$(dump | grep -o "text=\"$1\"[^>]*bounds=\"\[[0-9]*,[0-9]*\]\[[0-9]*,[0-9]*\]\"" | head -1 | grep -o '\[[0-9]*,[0-9]*\]\[[0-9]*,[0-9]*\]')
  [ -z "$b" ] && return 1
  set -- $(echo "$b" | sed 's/\]\[/,/; s/[][]//g' | tr ',' ' ')
  adb shell input tap $(( ($1 + $3) / 2 )) $(( ($2 + $4) / 2 ))
}

check() { # $1 nombre, $2 resultado (0 = ok)
  if [ "$2" -eq 0 ]; then echo "OK   $1"; else echo "FAIL $1"; status=1; fi
  adb exec-out screencap -p > "smoke/$(echo "$1" | tr ' /:' '___').png"
}

launch() { adb shell monkey -p "$1" -c android.intent.category.LAUNCHER 1 >/dev/null 2>&1; }

# El emulador termina de arrancar antes de tener internet; esperar a que la API responda.
for _ in $(seq 1 60); do
  adb shell ping -c 1 -W 2 rickandmortyapi.com >/dev/null 2>&1 && break
  sleep 2
done

# --- MVI y MVP: siguen abriendo y mostrando datos ---
for pkg in $APP.mvi $APP.mvp; do
  apk=app-${pkg##*.}/build/outputs/apk/release/app-${pkg##*.}-release.apk
  adb install -r "$apk" >/dev/null
  launch "$pkg"
  wait_text "Rick Sanchez"; check "$pkg lista" $?
  adb shell am force-stop "$pkg"
done

# --- :app (MVVM) con los 4 componentes ---
adb install -r app/build/outputs/apk/release/app-release.apk >/dev/null
adb shell pm grant $APP android.permission.POST_NOTIFICATIONS
adb logcat -c
launch $APP
wait_text "Rick Sanchez"; check "app lista" $?

# Activity: tocar la card abre el detalle (Intent explícito).
# (Se busca "Especie": en la pantalla chica del emulador "Compartir" queda fuera de la vista.)
tap_text "Rick Sanchez" && wait_text "Especie" 15; check "Activity detalle" $?
adb shell input keyevent KEYCODE_BACK
wait_text "Descargar todo" 10

# Service + broadcast propio: "Descargar todo" baja todas las páginas y avisa al terminar.
# (Antes del modo avión, para que la red no esté recién reconectándose.)
tap_text "Descargar todo"
result=1
for _ in $(seq 1 90); do
  ui=$(dump)
  if echo "$ui" | grep -q "Descarga completa"; then result=0; break; fi
  if echo "$ui" | grep -q "No se pudo completar la descarga"; then echo "  el Service avisó un error de descarga"; break; fi
  sleep 1
done
check "Service descarga + broadcast" $result

# BroadcastReceiver del sistema: modo avión muestra el aviso.
adb shell cmd connectivity airplane-mode enable
wait_text "Modo avión activado" 15; check "Broadcast modo avion" $?
adb shell cmd connectivity airplane-mode disable
for _ in $(seq 1 30); do adb shell ping -c 1 -W 2 rickandmortyapi.com >/dev/null 2>&1 && break; sleep 2; done

# ContentProvider cerrado: alguien sin la firma (adb) recibe Permission Denial.
adb shell content query --uri content://$APP.provider/characters 2>&1 | tee smoke/provider.txt | grep -q "Permission Denial"
check "ContentProvider cerrado" $?


if [ $status -ne 0 ]; then
  echo "--- Log del Service ---"
  adb logcat -d -s DownloadAllService:* | tail -40
  echo "--- Crashes / errores ---"
  adb logcat -d | grep -A40 "FATAL EXCEPTION" || adb logcat -d | grep -iE "androidsample|ForegroundService|okhttp|Exception" | tail -80
fi
exit $status
