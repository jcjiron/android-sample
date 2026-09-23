package com.jcjiron.androidsample.data.local

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val dao: CharacterDao,
) {
    fun observeCharacters(): Flow<List<CharacterEntity>> = dao.observeAll()

    suspend fun saveCharacters(characters: List<CharacterEntity>) = dao.upsertAll(characters)

    suspend fun replaceCharacters(characters: List<CharacterEntity>) = dao.replaceAll(characters)
}
