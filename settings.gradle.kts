pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AndroidSample"
// Capas compartidas
include(":core")          // Kotlin puro: domain + AppConfig + ejemplos de Kotlin (Android e iOS)
include(":ui")            // Compose Multiplatform sin estado: tema, pantallas, textos
include(":data-android")  // Retrofit + Room + Hilt (solo Android)
include(":data-kmp")      // Ktor + Room KMP + Koin (Android e iOS)

// Apps: mismo demo, distinto patrón / plataforma
include(":app")      // MVVM — la que se sube a Play Store
include(":app-mvi")  // MVI
include(":app-mvp")  // MVP
include(":app-kmp")  // Kotlin Multiplatform: Android + framework para iosApp/
