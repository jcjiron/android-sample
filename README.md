# android-sample

Demo que consume la [Rick and Morty API](https://rickandmortyapi.com/documentation).
El mismo demo está hecho **varias veces**: tres arquitecturas en Android (MVVM, MVI, MVP) y una
versión Kotlin Multiplatform que corre en **Android e iOS**. Cada app tiene tres pestañas:

1. **Personajes**: lista con scroll infinito, pull-to-refresh y caché offline.
2. **Variables**: los tipos de variables de Kotlin, con el resultado de correr cada ejemplo.
3. **Scope functions**: `let`, `run`, `with`, `apply`, `also`, `takeIf`/`takeUnless`.

## Módulos

```
                         ┌──────────────────────────┐
                         │  :core  (Kotlin puro)    │  ← lo usa TODO, incluido iOS
                         │  domain + AppConfig      │
                         └──────────────────────────┘
                            ▲          ▲          ▲
          ┌─────────────────┘          │          └─────────────────┐
┌─────────────────────┐    ┌────────────────────┐    ┌────────────────────┐
│ :data-android       │    │ :ui                │    │ :data-kmp          │
│ Retrofit, Room, Hilt│    │ Compose (Android   │    │ Ktor, Room KMP,    │
│                     │    │ + iOS), sin estado │    │ Koin               │
└─────────────────────┘    └────────────────────┘    └────────────────────┘
          ▲                     ▲            ▲                  ▲
   :app / :app-mvi / :app-mvp ──┘            └── :app-kmp + iosApp/
```

| Módulo | Qué es | Tipo |
|---|---|---|
| `:core` | `domain` (modelo + interfaz `CharacterRepository`), `AppConfig`, ejemplos de Kotlin. **Sin Android**: si alguien mete una clase de Android aquí, no compila | Kotlin Multiplatform (JVM + iOS) |
| `:ui` | Tema, pantallas sin estado (`CharactersContent`, `MainScaffold`, pestañas de Kotlin) y textos | Compose Multiplatform (Android + iOS) |
| `:data-android` | Implementación de `CharacterRepository` con Retrofit + Room + Hilt | Librería Android |
| `:data-kmp` | **La misma interfaz** implementada con Ktor + Room KMP + Koin | Kotlin Multiplatform (Android + iOS) |
| `:app` | **MVVM** (ViewModel + LiveData). Es la que se sube a Play Store | App Android |
| `:app-mvi` | **MVI** (State + Intent + Reducer, StateFlow) | App Android |
| `:app-mvp` | **MVP** (Contract + Presenter + View) | App Android |
| `:app-kmp` | **KMP** (MVVM con StateFlow): app Android + framework `ComposeApp` para iOS | Kotlin Multiplatform |
| `iosApp/` | Proyecto de Xcode (SwiftUI de ~20 líneas) que muestra la app de `:app-kmp` | iOS |

La idea central: `CharacterRepository` es una **interfaz en `:core` con dos implementaciones**,
una por módulo de datos. Las apps no saben cuál usan; solo cambia qué módulo de datos conectan.

### Qué cambia entre `:data-android` y `:data-kmp`

| Pieza | `:data-android` | `:data-kmp` | Por qué |
|---|---|---|---|
| Cliente HTTP | Retrofit | Ktor | Retrofit solo corre en la JVM |
| Base de datos | Room 2.6 | Room 2.7 (KMP) + SQLite empaquetado | Room 2.6 es solo Android |
| Inyección | Hilt (`@Module`, `@Provides`, `@Binds`) | Koin (`module { single { } }`) | Hilt depende de Android/Dagger |
| Partes por plataforma | — | `androidMain/` y `iosMain/`: motor HTTP y ruta de la base | Cada sistema guarda archivos y hace red distinto |

## Los patrones, lado a lado

| | MVVM (`:app`) | MVI (`:app-mvi`) | MVP (`:app-mvp`) | KMP (`:app-kmp`) |
|---|---|---|---|---|
| Quién maneja el estado | `CharactersViewModel` | `CharactersViewModel` + `CharactersReducer` | `CharactersPresenter` | `CharactersViewModel` (compartido Android/iOS) |
| Cómo llega a la UI | Varios `LiveData` | **Un** `StateFlow<CharactersState>` | `view.showX(...)` | **Un** `StateFlow<CharactersUiState>` |
| Cómo llegan los eventos | `refresh()`, `loadMore()` | **Un** `onIntent(...)` | `onRefresh()`, `onLoadMore()` | `refresh()`, `loadMore()` |
| ¿Conoce a la View? | No | No | **Sí** | No |
| Sobrevive a rotar | Sí | Sí | No (vive con la Activity) | Sí |
| DI | Hilt | Hilt | Hilt | Koin |

## Estructura

```
core/src/commonMain/kotlin/.../core/
├── AppConfig.kt
├── domain/model/Character.kt
├── domain/repository/CharacterRepository.kt   ← la interfaz
└── kotlinbasics/                               ← VariableExamples, ScopeFunctionExamples

ui/src/commonMain/
├── kotlin/.../ui/  theme/ characters/CharactersContent.kt  main/MainScaffold.kt  kotlinbasics/
└── composeResources/values/strings.xml         ← Res.string.xxx
ui/src/androidMain/res/                          ← ícono de las apps Android

data-android/src/main/java/.../data/
├── remote/  (Retrofit)   local/  (Room)   repository/CharacterRepositoryImpl.kt
└── di/      NetworkModule, DatabaseModule, RepositoryModule (Hilt)

data-kmp/src/
├── commonMain/.../datakmp/ remote/ (Ktor)  local/ (Room KMP)  repository/  di/ (Koin)
├── androidMain/.../di/PlatformModule.android.kt   ← OkHttp + base con Context
└── iosMain/.../di/PlatformModule.ios.kt           ← Darwin + carpeta de documentos

app/       → presentation/characters/CharactersViewModel.kt (MVVM + LiveData)
app-mvi/   → presentation/characters/CharactersContract.kt, CharactersReducer.kt, CharactersViewModel.kt
app-mvp/   → presentation/characters/CharactersContract.kt, CharactersPresenter.kt; di/PresenterModule.kt
app-kmp/   → commonMain/ App.kt, CharactersViewModel.kt, di/AppModule.kt
             androidMain/ KmpApp.kt, MainActivity.kt
             iosMain/ MainViewController.kt
iosApp/    → project.yml (XcodeGen), iosApp/iOSApp.swift, iosApp/ContentView.swift
signing/   → llaves de firma (no van en git)
```

## Publicar en Play Store

### Firma

Las llaves van en `signing/` y `app/build.gradle.kts` las carga desde ahí. El paso a paso para
generarlas en Android Studio está en [`signing/README.md`](signing/README.md). En resumen:

1. Generar `signing/release.jks` (alias `upload`).
2. Copiar `signing/keystore.properties.example` → `signing/keystore.properties` y poner las contraseñas.
3. `./gradlew :app:bundleRelease` → `app/build/outputs/bundle/release/app-release.aab`.

Sin llaves el proyecto sigue compilando: el release se firma con la llave debug y Gradle avisa.

### R8 / ProGuard

- `release` tiene `isMinifyEnabled = true` e `isShrinkResources = true`.
- Las reglas de Retrofit, kotlinx.serialization y los modelos (`-keep` de los DTOs en
  `data/remote` y de los `*Entity` de Room) están en `data-android/consumer-rules.pro`. Viajan
  solas a cada app que usa `:data-android`. Las de Ktor y los modelos KMP están en
  `data-kmp/consumer-rules.pro`.
- `app/proguard-rules.pro` conserva números de línea para los stack traces. Subir
  `app/build/outputs/mapping/release/mapping.txt` a Play Console para desofuscar crashes.
- **Probar el release en un teléfono antes de subirlo**: `./gradlew :app:installRelease`.

## Build types y flavors

- `debug`: `applicationIdSuffix = ".debug"`, logging HTTP completo.
- `release`: sin sufijo, R8 activado.
- Cada build type define `BuildConfig.BASE_URL` con `buildConfigField` (`buildConfig = true`).
- Los product flavors (`dev` / `prod`) están declarados y comentados en `app/build.gradle.kts`.

## Correr

```bash
./gradlew :app:installDebug       # MVVM
./gradlew :app-mvi:installDebug   # MVI
./gradlew :app-mvp:installDebug   # MVP
./gradlew :app-kmp:installDebug   # KMP en Android
```

En Android Studio, elegir el módulo en el selector de configuración junto al botón Run.
Requiere JDK 17 y Android SDK 35.

### iOS (necesita una Mac con Xcode)

El proyecto de Xcode se genera con [XcodeGen](https://github.com/yonaskolb/XcodeGen) a partir de
`iosApp/project.yml` (así no hay que versionar el `.xcodeproj`, que es enorme y da conflictos):

```bash
brew install xcodegen
cd iosApp && xcodegen       # crea iosApp.xcodeproj
open iosApp.xcodeproj       # Run ▶ en un simulador de iPhone
```

Xcode compila primero el código Kotlin (`:app-kmp:embedAndSignAppleFrameworkForXcode`) y luego Swift.
La primera vez tarda varios minutos porque descarga el compilador de Kotlin/Native.
Para un iPhone físico hay que elegir tu equipo de desarrollo en *Signing & Capabilities*.

## CI

- **CI** (`.github/workflows/ci.yml`): compila debug y release (R8) de todas las apps Android,
  el AAB de `:app`, prueba la firma con una llave temporal, y en macOS compila el framework de
  Kotlin/Native y la app de iOS con Xcode.
- **Smoke test** (`.github/workflows/smoke.yml`): abre cada app de verdad. Instala los release
  (con R8) de las 4 apps Android en un emulador y espera a ver "Rick Sanchez" en pantalla; instala
  `iosApp` en un simulador de iPhone y revisa que siga viva. Corre en ramas de trabajo y a mano
  desde la pestaña Actions.

---

## Android Rules

Así construyo aplicaciones Android. Una regla, una línea.

### Arquitectura

- Clean Architecture en un solo módulo, con paquetes por capa: `data`, `domain`, `presentation`, `di`.
  *(Este demo es multimódulo a propósito para comparar arquitecturas y plataformas: las capas viven en `:core`, `:data-*` y `:ui`.)*
- Las dependencias apuntan hacia adentro: `presentation` → `domain` ← `data`; `domain` no conoce Android.
- MVVM con ViewModels de Jetpack; nada de MVP ni listeners actualizados a mano.
- El ViewModel expone estado con `StateFlow` y la UI solo lo observa.
  *(En este demo se usa `LiveData` / `MutableLiveData` para mostrar el patrón clásico.)*

### Datos

- Un `RemoteDataSource` que habla con Retrofit y un `LocalDataSource` que habla con Room, siempre separados.
- Un `Repository` orquesta ambos data sources; el ViewModel nunca sabe de dónde viene el dato.
- Retrofit es el cliente HTTP por default.
- Room es la base de datos local por default.

### Inyección de dependencias

- Hilt desde el primer commit; ninguna dependencia se instancia a mano.
- Los módulos de Hilt viven en el paquete `di`, uno por responsabilidad (red, base de datos, repositorios).

### UI y tema

- Jetpack Compose para toda la UI, sin layouts en XML.
- `LightColors` y `DarkColors` viven en variables separadas para cambiar de tema en una línea.
- El tema define colores, tipografía, shapes y surfaces; ningún composable hardcodea estilos.

### Build

- Gradle con Kotlin DSL (`.kts`) y dependencias en version catalog (`libs.versions.toml`).
- Dos build types: `debug` con `applicationIdSuffix = ".debug"` y `release` sin sufijo.
- Cada build type inyecta su URL de servidor vía `buildConfigField`, con `buildConfig = true` activado.
- Los product flavors se declaran desde el inicio, aunque sea comentados, porque tarde o temprano se ocupan.
