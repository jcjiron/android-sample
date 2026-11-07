package com.jcjiron.androidsample.core.data.local

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val dao: CharacterDao,
) {
    fun observeCharacters(): Flow<List<CharacterEntity>> = dao.observeAll()

    fun observeCharacter(id: Int): Flow<CharacterEntity?> = dao.observeById(id)

    suspend fun count(): Int = dao.count()

    suspend fun saveCharacters(characters: List<CharacterEntity>) = dao.upsertAll(characters)

    suspend fun replaceCharacters(characters: List<CharacterEntity>) = dao.replaceAll(characters)
}
