package com.jcjiron.androidsample.datakmp.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * Con Ktor no hay interfaz con anotaciones como en Retrofit: es una clase normal
 * que arma la petición a mano. La URL base viene del HttpClient (defaultRequest).
 */
class RickAndMortyApi(private val client: HttpClient) {

    suspend fun getCharacters(page: Int): CharacterPageDto =
        client.get("character") { parameter("page", page) }.body()
}
