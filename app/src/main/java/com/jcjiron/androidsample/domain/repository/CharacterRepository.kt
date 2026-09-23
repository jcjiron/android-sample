package com.jcjiron.androidsample.domain.repository

import com.jcjiron.androidsample.domain.model.Character
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {

    /** Fuente única de verdad: emite lo que haya en caché cada vez que cambia. */
    fun observeCharacters(): Flow<List<Character>>

    /**
     * Descarga una página del servidor y la guarda en caché.
     * La página 1 reemplaza la caché completa. Regresa `true` si hay más páginas.
     */
    suspend fun fetchPage(page: Int): Result<Boolean>
}
