package com.jcjiron.androidsample.core.domain.repository

import com.jcjiron.androidsample.core.domain.model.Character
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {

    /** Fuente única de verdad: emite lo que haya en caché cada vez que cambia. */
    fun observeCharacters(): Flow<List<Character>>

    /** Un solo personaje de la caché (o null si no está guardado). */
    fun observeCharacter(id: Int): Flow<Character?>

    /**
     * Descarga una página del servidor y la guarda en caché.
     * La página 1 reemplaza la caché completa. Regresa `true` si hay más páginas.
     */
    suspend fun fetchPage(page: Int): Result<Boolean>

    /**
     * Descarga TODAS las páginas a la caché para usar la app sin internet.
     * Avisa el avance con [onProgress] y regresa cuántos personajes quedaron guardados.
     */
    suspend fun downloadAll(onProgress: suspend (page: Int, totalPages: Int) -> Unit): Result<Int>
}
