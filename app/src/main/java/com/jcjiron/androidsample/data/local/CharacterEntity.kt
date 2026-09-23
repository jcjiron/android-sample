package com.jcjiron.androidsample.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jcjiron.androidsample.domain.model.Character
import com.jcjiron.androidsample.domain.model.CharacterStatus

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val origin: String,
    val location: String,
    val imageUrl: String,
)

fun CharacterEntity.toDomain() = Character(
    id = id,
    name = name,
    status = when (status.lowercase()) {
        "alive" -> CharacterStatus.ALIVE
        "dead" -> CharacterStatus.DEAD
        else -> CharacterStatus.UNKNOWN
    },
    species = species,
    gender = gender,
    origin = origin,
    location = location,
    imageUrl = imageUrl,
)
