import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// :data-kmp: cómo se obtienen los datos en Android E iOS.
// Implementa el mismo CharacterRepository de :core, pero con librerías multiplataforma:
// Ktor (en vez de Retrofit), Room KMP (en vez de Room 2.6) y Koin (en vez de Hilt).
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
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

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":core"))
            api(libs.koin.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.room.kmp.runtime)
            implementation(libs.sqlite.bundled)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "com.jcjiron.androidsample.datakmp"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

ksp {
    arg("room.generateKotlin", "true")
}

// Room genera el código de la base de datos para cada plataforma.
dependencies {
    add("kspAndroid", libs.room.kmp.compiler)
    add("kspIosX64", libs.room.kmp.compiler)
    add("kspIosArm64", libs.room.kmp.compiler)
    add("kspIosSimulatorArm64", libs.room.kmp.compiler)
}
