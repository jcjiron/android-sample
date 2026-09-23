package com.jcjiron.androidsample.data.remote

import com.jcjiron.androidsample.data.local.CharacterEntity
import kotlinx.serialization.Serializable

@Serializable
data class CharacterPageDto(
    val info: PageInfoDto,
    val results: List<CharacterDto>,
)

@Serializable
data class PageInfoDto(
    val count: Int,
    val pages: Int,
    val next: String? = null,
    val prev: String? = null,
)

@Serializable
data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val origin: LocationDto,
    val location: LocationDto,
    val image: String,
)

@Serializable
data class LocationDto(
    val name: String,
)

fun CharacterDto.toEntity() = CharacterEntity(
    id = id,
    name = name,
    status = status,
    species = species,
    gender = gender,
    origin = origin.name,
    location = location.name,
    imageUrl = image,
)
