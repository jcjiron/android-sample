# android-sample

App de ejemplo en Android que consume la [Rick and Morty API](https://rickandmortyapi.com/documentation)
y sigue las reglas de arquitectura de abajo. Muestra la lista de personajes con scroll infinito,
pull-to-refresh y caché offline en Room (si no hay red, se ve lo último guardado).

## Stack

| Tema | Elección |
|---|---|
| Arquitectura | Clean Architecture en un módulo (`data`, `domain`, `presentation`, `di`) + MVVM |
| Estado | `StateFlow` expuesto por `MainViewModel`, observado con `collectAsStateWithLifecycle` |
| Red | Retrofit + OkHttp + kotlinx.serialization |
| Base de datos | Room (fuente única de verdad) |
| DI | Hilt (`NetworkModule`, `DatabaseModule`, `RepositoryModule`) |
| UI | Jetpack Compose + Material 3, sin XML de layouts |
| Imágenes | Coil |
| Build | Gradle Kotlin DSL + `libs.versions.toml` |

## Flujo de datos

```
MainScreen ──observa──▶ MainViewModel (StateFlow<MainUiState>)
                              │
                              ▼
                  CharacterRepository (domain, interfaz)
                              │
                  CharacterRepositoryImpl (data)
                     │                     │
             RemoteDataSource        LocalDataSource
               (Retrofit)               (Room)
```

1. La UI observa `uiState`; nunca sabe de dónde viene el dato.
2. El repositorio expone `observeCharacters()` desde Room.
3. `fetchPage(n)` baja la página del API y la guarda en Room; Room emite y la UI se actualiza sola.

## Estructura

```
app/src/main/java/com/jcjiron/androidsample/
├── SampleApp.kt              # @HiltAndroidApp
├── data/
│   ├── local/                # CharacterEntity, CharacterDao, AppDatabase, LocalDataSource
│   ├── remote/               # RickAndMortyApi, DTOs, RemoteDataSource
│   └── repository/           # CharacterRepositoryImpl
├── domain/
│   ├── model/                # Character, CharacterStatus
│   └── repository/           # CharacterRepository
├── presentation/
│   ├── theme/                # Color.kt, Type.kt, Shape.kt, Theme.kt
│   └── main/                 # MainActivity, MainViewModel, MainUiState, MainScreen
└── di/                       # NetworkModule, DatabaseModule, RepositoryModule
```

## Build types y flavors

- `debug`: `applicationIdSuffix = ".debug"`, logging HTTP completo. Se puede instalar junto al release.
- `release`: sin sufijo, firmado con la llave debug solo para el demo.
- Cada build type define `BuildConfig.BASE_URL` con `buildConfigField` (`buildConfig = true`).
- Los product flavors (`dev` / `prod`) están declarados y comentados en `app/build.gradle.kts`.

## Tema

`LightColors` y `DarkColors` viven en `presentation/theme/Color.kt`. En `Theme.kt` se elige el
esquema en una línea; tipografía (`AppTypography`), shapes (`AppShapes`) y espaciados
(`AppTheme.dimens`) salen del tema, así que ningún composable hardcodea estilos.
`MainScreen.kt` trae previews en claro y oscuro.

## Correr

```bash
./gradlew installDebug        # o abrir en Android Studio y darle Run
./gradlew assembleRelease
```

Requiere JDK 17 y Android SDK 35.

---

## Android Rules

Así construyo aplicaciones Android. Una regla, una línea.

### Arquitectura

- Clean Architecture en un solo módulo, con paquetes por capa: `data`, `domain`, `presentation`, `di`.
- Las dependencias apuntan hacia adentro: `presentation` → `domain` ← `data`; `domain` no conoce Android.
- MVVM con ViewModels de Jetpack; nada de MVP ni listeners actualizados a mano.
- El ViewModel expone estado con `StateFlow` y la UI solo lo observa.

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
