import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

// ---------------------------------------------------------------------------
// Firmado: las llaves viven en <raíz del proyecto>/signing (ver signing/README.md).
//
// Cada valor se busca primero en una variable de entorno (para CI) y luego en
// signing/keystore.properties. Si el keystore no existe, está vacío o le faltan
// datos, se ignora y el build sigue funcionando.
// ---------------------------------------------------------------------------
val signingDir: File = rootProject.file("signing")

val signingProperties = Properties().apply {
    val file = File(signingDir, "keystore.properties")
    if (file.isFile) file.inputStream().use { load(it) }
}

fun signingValue(name: String): String? =
    (System.getenv(name) ?: signingProperties.getProperty(name))?.takeIf { it.isNotBlank() }

/** Regresa el keystore `<prefix>_STORE_FILE` solo si existe, no está vacío y tiene sus 3 datos. */
fun keystoreOrNull(prefix: String): File? {
    val file = signingValue("${prefix}_STORE_FILE")?.let { File(signingDir, it) } ?: return null
    val isComplete = listOf("STORE_PASSWORD", "KEY_ALIAS", "KEY_PASSWORD")
        .all { signingValue("${prefix}_$it") != null }
    return file.takeIf { it.isFile && it.length() > 0 && isComplete }
}

val releaseKeystore = keystoreOrNull("RELEASE")
val debugKeystore = keystoreOrNull("DEBUG")

android {
    namespace = "com.jcjiron.androidsample"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jcjiron.androidsample"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    signingConfigs {
        // Opcional: un debug keystore compartido para que todo el equipo tenga la misma SHA-1
        // (Firebase, Google Maps, etc.). Si no hay, Android Studio usa ~/.android/debug.keystore.
        getByName("debug") {
            if (debugKeystore != null) {
                storeFile = debugKeystore
                storePassword = signingValue("DEBUG_STORE_PASSWORD")
                keyAlias = signingValue("DEBUG_KEY_ALIAS")
                keyPassword = signingValue("DEBUG_KEY_PASSWORD")
            }
        }
        // Llave de subida (upload key) para Play Store.
        create("release") {
            if (releaseKeystore != null) {
                storeFile = releaseKeystore
                storePassword = signingValue("RELEASE_STORE_PASSWORD")
                keyAlias = signingValue("RELEASE_KEY_ALIAS")
                keyPassword = signingValue("RELEASE_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            buildConfigField("String", "BASE_URL", "\"https://rickandmortyapi.com/api/\"")
        }
        release {
            // R8: reduce, optimiza y ofusca el código; quita recursos que no se usan.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            buildConfigField("String", "BASE_URL", "\"https://rickandmortyapi.com/api/\"")
            signingConfig = if (releaseKeystore != null) {
                signingConfigs.getByName("release")
            } else {
                // Sin llave de producción todavía: se firma con la de debug para poder probar el
                // release en un teléfono. Play Store rechaza este APK/AAB, así que no hay riesgo.
                logger.warn("signing/: no hay keystore de release; el release se firma con la llave debug.")
                signingConfigs.getByName("debug")
            }
        }
    }

    // Declarados desde el inicio: descomentar cuando se necesiten ambientes o marcas distintas.
    // flavorDimensions += "environment"
    // productFlavors {
    //     create("dev") {
    //         dimension = "environment"
    //         applicationIdSuffix = ".dev"
    //         buildConfigField("String", "BASE_URL", "\"https://dev.example.com/api/\"")
    //     }
    //     create("prod") {
    //         dimension = "environment"
    //         buildConfigField("String", "BASE_URL", "\"https://rickandmortyapi.com/api/\"")
    //     }
    // }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":data-android"))
    implementation(project(":ui"))
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.runtime.livedata)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
}
