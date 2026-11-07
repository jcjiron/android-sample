package com.jcjiron.androidsample.core.data.local

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CharacterDao {

    @Query("SELECT * FROM characters ORDER BY id ASC")
    abstract fun observeAll(): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE id = :id")
    abstract fun observeById(id: Int): Flow<CharacterEntity?>

    @Query("SELECT COUNT(*) FROM characters")
    abstract suspend fun count(): Int

    // Versiones con Cursor: es lo que regresa un ContentProvider a otras apps.
    @Query("SELECT * FROM characters ORDER BY id ASC")
    abstract fun selectAllCursor(): Cursor

    @Query("SELECT * FROM characters WHERE id = :id")
    abstract fun selectByIdCursor(id: Int): Cursor

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
