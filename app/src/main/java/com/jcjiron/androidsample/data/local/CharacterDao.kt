package com.jcjiron.androidsample.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CharacterDao {

    @Query("SELECT * FROM characters ORDER BY id ASC")
    abstract fun observeAll(): Flow<List<CharacterEntity>>

    @Upsert
    abstract suspend fun upsertAll(characters: List<CharacterEntity>)

    @Query("DELETE FROM characters")
    abstract suspend fun deleteAll()

    @Transaction
    open suspend fun replaceAll(characters: List<CharacterEntity>) {
        deleteAll()
        upsertAll(characters)
    }
}
