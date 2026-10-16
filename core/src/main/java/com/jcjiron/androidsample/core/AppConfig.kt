package com.jcjiron.androidsample.core

/**
 * Configuración que cada app le da a `:core` vía Hilt.
 * Así la URL del servidor sigue saliendo del `buildConfigField` de cada app y build type.
 */
data class AppConfig(
    val baseUrl: String,
    val isDebug: Boolean,
)
