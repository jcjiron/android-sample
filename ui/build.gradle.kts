import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// :ui es Compose Multiplatform: tema, pantallas sin estado y textos.
// La usan las apps de Android (MVVM, MVI, MVP, KMP) y la app de iOS.
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(project(":core"))
            api(compose.runtime)
            api(compose.foundation)
            api(compose.ui)
            api(compose.material3)
            api(compose.components.resources)
            implementation(libs.compose.mp.material.icons.core)
            implementation(libs.coil3.compose)
        }
        androidMain.dependencies {
            implementation(libs.coil3.network.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.coil3.network.ktor3)
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "com.jcjiron.androidsample.ui"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// Genera la clase `Res` (Res.string.retry, etc.) pública para que las apps también la usen.
compose.resources {
    publicResClass = true
    packageOfResClass = "com.jcjiron.androidsample.ui.resources"
    generateResClass = always
}
