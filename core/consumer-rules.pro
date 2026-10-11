# Reglas de R8/ProGuard de :core. Se aplican solas a cada app que depende de este módulo
# (consumerProguardFiles), así que viven junto al código que protegen.

# ---------------------------------------------------------------------------
# Modelos: DTOs (JSON de la API) y Entities (Room).
# Se conservan completos para que R8 no los renombre ni los borre y el
# serializador / Room los sigan encontrando.
# ---------------------------------------------------------------------------
-keep class com.jcjiron.androidsample.core.data.remote.** { *; }
-keep class com.jcjiron.androidsample.core.data.local.*Entity { *; }

# ---------------------------------------------------------------------------
# Retrofit (https://github.com/square/retrofit/blob/trunk/retrofit/src/main/resources/META-INF/proguard/retrofit2.pro)
# Retrofit lee por reflexión los genéricos y anotaciones de la interfaz del API.
# ---------------------------------------------------------------------------
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
# Necesario para las funciones `suspend` del API.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# La interfaz del API, explícita.
-keep interface com.jcjiron.androidsample.core.data.remote.RickAndMortyApi { *; }

# ---------------------------------------------------------------------------
# kotlinx.serialization (https://github.com/Kotlin/kotlinx.serialization#android)
# ---------------------------------------------------------------------------
-keepattributes *Annotation*
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.jcjiron.androidsample.core.data.remote.**$$serializer { *; }
-keepclassmembers class com.jcjiron.androidsample.core.data.remote.** { *** Companion; }
-keepclasseswithmembers class com.jcjiron.androidsample.core.data.remote.** {
    kotlinx.serialization.KSerializer serializer(...);
}
