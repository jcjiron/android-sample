# Reglas de R8/ProGuard propias de esta app.
#
# Las reglas de Retrofit, kotlinx.serialization y los modelos (DTOs y Entities) viven en
# core/consumer-rules.pro y se aplican solas porque :app depende de :core.
# Hilt, Room, Compose, OkHttp y Coil traen sus propias reglas dentro de su .aar.

# Conserva número de línea en los stack traces de Play Console / Crashlytics.
# El archivo mapping.txt (app/build/outputs/mapping/release/) se sube a Play para desofuscar.
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile
