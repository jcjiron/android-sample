# signing/

Aquí viven las llaves con las que se firma la app. `app/build.gradle.kts` las lee de esta carpeta.

```
signing/
├── README.md                     # este archivo (sí va en git)
├── keystore.properties.example   # plantilla (sí va en git)
├── keystore.properties           # contraseñas reales   ← NO va en git
├── release.jks                   # llave de producción  ← NO va en git
└── debug.jks                     # opcional             ← NO va en git
```

Si `release.jks` no existe, está vacío o le falta algún dato en `keystore.properties`, el build
**no truena**: firma el release con la llave debug y muestra un warning. Así el proyecto compila
en cualquier máquina aunque no tenga las llaves.

## ¿Hay llave de desarrollo?

Sí, pero no hace falta crearla: Android Studio genera una sola la primera vez en
`~/.android/debug.keystore` (contraseña `android`, alias `androiddebugkey`). Cada desarrollador
tiene la suya, con SHA-1 distinta. Solo vale la pena poner un `debug.jks` compartido aquí si un
servicio (Firebase, Google Maps, login con Google) necesita registrar la SHA-1 de debug.

## Paso a paso: generar la llave de producción en Android Studio

1. **Build → Generate Signed App Bundle / APK…**
2. Elegir **Android App Bundle** (Play Store pide `.aab`) → **Next**.
3. En *Key store path* dar clic en **Create new…**
   - *Key store path*: `<proyecto>/signing/release.jks`
   - *Password* / *Confirm*: contraseña del keystore.
   - *Alias*: `upload`
   - *Password* de la llave (puede ser la misma).
   - *Validity (years)*: `25` o más (Google pide que dure más allá de 2033).
   - *Certificate*: al menos *First and Last Name* y *Country Code* (`MX`).
   - **OK**.
4. Cerrar el asistente (no hace falta terminar el build desde ahí).
5. Copiar `keystore.properties.example` como `keystore.properties` y llenar:
   ```properties
   RELEASE_STORE_FILE=release.jks
   RELEASE_STORE_PASSWORD=<contraseña del keystore>
   RELEASE_KEY_ALIAS=upload
   RELEASE_KEY_PASSWORD=<contraseña de la llave>
   ```
6. Generar el bundle firmado:
   ```bash
   ./gradlew :app:bundleRelease
   # sale en app/build/outputs/bundle/release/app-release.aab
   ```

Lo mismo desde terminal, sin Android Studio:

```bash
keytool -genkeypair -v \
  -keystore signing/release.jks \
  -alias upload \
  -keyalg RSA -keysize 2048 -validity 10000
```

Para revisar qué hay dentro (y sacar la SHA-1 / SHA-256):

```bash
keytool -list -v -keystore signing/release.jks -alias upload
```

## Play App Signing (importante)

Al subir la primera versión, Play Console activa **Play App Signing**:

- La llave que generaste aquí es la **upload key**: solo sirve para subirle builds a Google.
- Google guarda la **app signing key**, que es con la que de verdad se firma lo que llega a los
  teléfonos.
- Si la upload key se pierde o se filtra, se puede pedir a Google que la cambie desde
  Play Console. Aun así: **respáldala** (gestor de contraseñas, bóveda de la empresa) junto con
  sus contraseñas.

## ¿Por qué no van en git?

Quien tenga el `.jks` y sus contraseñas puede firmar builds como si fuera la empresa. Por eso
`.gitignore` excluye `*.jks`, `*.keystore` y `keystore.properties` de esta carpeta. En CI se pasan
como *secrets* (el keystore en base64 y las contraseñas como variables de entorno con los mismos
nombres de `keystore.properties`).
