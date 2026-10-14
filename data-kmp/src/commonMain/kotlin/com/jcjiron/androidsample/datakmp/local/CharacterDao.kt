package com.jcjiron.androidsample.datakmp.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/** En Room KMP todas las funciones deben ser `suspend` o regresar `Flow`. */
@Dao
interface CharacterDao {

    @Query("SELECT * FROM characters ORDER BY id ASC")
    fun observeAll(): Flow<List<CharacterEntity>>

    @Upsert
    suspend fun upsertAll(characters: List<CharacterEntity>)

    @Query("DELETE FROM characters WHERE id NOT IN (:ids)")
    suspend fun deleteAllExcept(ids: List<Int>)
}
