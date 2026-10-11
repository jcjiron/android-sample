# android-sample

Demo de Android que consume la [Rick and Morty API](https://rickandmortyapi.com/documentation).
El mismo demo está hecho **tres veces, con tres arquitecturas distintas**, para comparar patrones
resolviendo exactamente el mismo problema. Cada app tiene tres pestañas:

1. **Personajes**: lista con scroll infinito, pull-to-refresh y caché offline en Room.
2. **Variables**: los tipos de variables de Kotlin, con el resultado de correr cada ejemplo.
3. **Scope functions**: `let`, `run`, `with`, `apply`, `also`, `takeIf`/`takeUnless`.

## Módulos

| Módulo | Qué es | Application ID |
|---|---|---|
| `:core` | Librería compartida: `domain`, `data`, `di`, tema y UI sin estado | — |
| `:app` | **MVVM** (ViewModel + LiveData). Es la que se sube a Play Store | `com.jcjiron.androidsample` |
| `:app-mvi` | **MVI** (State + Intent + Reducer, StateFlow) | `com.jcjiron.androidsample.mvi` |
| `:app-mvp` | **MVP** (Contract + Presenter + View) | `com.jcjiron.androidsample.mvp` |

Las tres apps comparten datos, tema y la UI de la lista (`CharactersContent`, que no tiene estado).
**Lo único que cambia es quién maneja el estado**, y eso es lo que se compara. Las tres se pueden
instalar a la vez en el mismo teléfono (*R&M MVVM*, *R&M MVI*, *R&M MVP*).

```
:app  ─┐
:app-mvi ─┼──▶ :core  (domain ← data, di, theme, UI sin estado)
:app-mvp ─┘
```

## Los tres patrones, lado a lado

| | MVVM (`:app`) | MVI (`:app-mvi`) | MVP (`:app-mvp`) |
|---|---|---|---|
| Quién maneja el estado | `CharactersViewModel` | `CharactersViewModel` + `CharactersReducer` | `CharactersPresenter` |
| Cómo llega el estado a la UI | Varios `LiveData` (uno por dato) | **Un** `StateFlow<CharactersState>` | El Presenter llama `view.showX(...)` |
| Cómo llegan los eventos | Funciones: `refresh()`, `loadMore()` | **Un** `onIntent(CharactersIntent)` | Funciones: `onRefresh()`, `onLoadMore()` |
| ¿Conoce a la View? | No | No | **Sí** (referencia a `CharactersContract.View`) |
| Sobrevive a rotar | Sí (ViewModel de Jetpack) | Sí (ViewModel de Jetpack) | No: vive con la Activity, se engancha con `attachView`/`detachView` |
| Lo más fácil de probar | El ViewModel | El reducer (función pura) | El Presenter con una View falsa |

Archivos clave:

- MVVM: `app/.../presentation/characters/CharactersViewModel.kt`
- MVI: `app-mvi/.../presentation/characters/CharactersContract.kt`, `CharactersReducer.kt`, `CharactersViewModel.kt`
- MVP: `app-mvp/.../presentation/characters/CharactersContract.kt`, `CharactersPresenter.kt`, `CharactersScreen.kt`

## Stack

| Tema | Elección |
|---|---|
| Red | Retrofit + OkHttp + kotlinx.serialization |
| Base de datos | Room (fuente única de verdad) |
| DI | Hilt (`NetworkModule`, `DatabaseModule`, `RepositoryModule` en `:core`; `AppConfigModule` en cada app) |
| UI | Jetpack Compose + Material 3, sin XML de layouts |
| Imágenes | Coil |
| Build | Gradle Kotlin DSL + `libs.versions.toml` |

## Flujo de datos

```
UI (Compose) ──▶ ViewModel / Presenter ──▶ CharacterRepository (domain)
                                                │
                                   CharacterRepositoryImpl (data)
                                      │                     │
                              RemoteDataSource        LocalDataSource
                                (Retrofit)               (Room)
```

1. El repositorio expone `observeCharacters()` desde Room.
2. `fetchPage(n)` baja la página del API y la guarda en Room; Room emite y la UI se actualiza sola.
3. Cada app le da a `:core` su `AppConfig` (URL y modo debug) desde su propio `BuildConfig`.

## Estructura

```
core/src/main/java/com/jcjiron/androidsample/core/
├── AppConfig.kt
├── data/            # local (Room), remote (Retrofit), repository
├── domain/          # model, repository (interfaces)
├── di/              # NetworkModule, DatabaseModule, RepositoryModule
└── presentation/
    ├── theme/       # Color.kt, Type.kt, Shape.kt, Theme.kt
    ├── characters/  # CharactersContent (UI sin estado)
    ├── kotlinbasics/
    └── main/        # MainScaffold (pestañas)

app/       → presentation/characters/CharactersViewModel.kt (MVVM)
app-mvi/   → presentation/characters/CharactersContract.kt, CharactersReducer.kt, CharactersViewModel.kt
app-mvp/   → presentation/characters/CharactersContract.kt, CharactersPresenter.kt; di/PresenterModule.kt
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
  `data/remote` y de los `*Entity` de Room) están en `core/consumer-rules.pro`. Viajan solas a
  cada app que usa `:core`.
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
```

En Android Studio, elegir el módulo en el selector de configuración junto al botón Run.
Requiere JDK 17 y Android SDK 35.

---

## Android Rules

Así construyo aplicaciones Android. Una regla, una línea.

### Arquitectura

- Clean Architecture en un solo módulo, con paquetes por capa: `data`, `domain`, `presentation`, `di`.
  *(Este demo es multimódulo a propósito para comparar arquitecturas; `:core` conserva los mismos paquetes.)*
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
