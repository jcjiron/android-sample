# Reglas de R8/ProGuard de :data-kmp (solo aplican a Android).

# Modelos: DTOs (JSON) y Entities (Room).
-keep class com.jcjiron.androidsample.datakmp.remote.** { *; }
-keep class com.jcjiron.androidsample.datakmp.local.*Entity { *; }

# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-keep,includedescriptorclasses class com.jcjiron.androidsample.datakmp.remote.**$$serializer { *; }
-keepclassmembers class com.jcjiron.androidsample.datakmp.remote.** { *** Companion; }
-keepclasseswithmembers class com.jcjiron.androidsample.datakmp.remote.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Ktor hace referencia a clases de la JVM de escritorio que no existen en Android.
-dontwarn java.lang.management.**
-dontwarn org.slf4j.**
