import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// :core es Kotlin puro: no aplica ningún plugin de Android.
// Si alguien mete una clase de Android aquí, simplemente no compila.
// - jvm(): lo consumen los módulos de Android (Android entiende bytecode de JVM).
// - ios*(): lo consume la app de iOS.
plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.coroutines.core)
        }
    }
}
