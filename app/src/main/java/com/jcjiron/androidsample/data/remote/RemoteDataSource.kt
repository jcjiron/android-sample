package com.jcjiron.androidsample.data.remote

import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val api: RickAndMortyApi,
) {
    suspend fun getCharacters(page: Int): CharacterPageDto = api.getCharacters(page)
}
