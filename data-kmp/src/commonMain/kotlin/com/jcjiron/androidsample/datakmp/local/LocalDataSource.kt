package com.jcjiron.androidsample.datakmp.local

import kotlinx.coroutines.flow.Flow

class LocalDataSource(
    private val dao: CharacterDao,
) {
    fun observeCharacters(): Flow<List<CharacterEntity>> = dao.observeAll()

    suspend fun saveCharacters(characters: List<CharacterEntity>) = dao.upsertAll(characters)

    /** Guarda la página nueva y luego borra lo viejo, así la lista nunca se queda vacía a medias. */
    suspend fun replaceCharacters(characters: List<CharacterEntity>) {
        dao.upsertAll(characters)
        dao.deleteAllExcept(characters.map { it.id })
    }
}
