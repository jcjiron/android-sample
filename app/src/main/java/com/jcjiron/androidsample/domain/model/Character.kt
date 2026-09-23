package com.jcjiron.androidsample.domain.model

data class Character(
    val id: Int,
    val name: String,
    val status: CharacterStatus,
    val species: String,
    val gender: String,
    val origin: String,
    val location: String,
    val imageUrl: String,
)

enum class CharacterStatus {
    ALIVE,
    DEAD,
    UNKNOWN,
}
