package com.jcjiron.androidsample.datakmp.remote

class RemoteDataSource(
    private val api: RickAndMortyApi,
) {
    suspend fun getCharacters(page: Int): CharacterPageDto = api.getCharacters(page)
}
