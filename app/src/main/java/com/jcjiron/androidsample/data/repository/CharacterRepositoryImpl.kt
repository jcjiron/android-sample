package com.jcjiron.androidsample.data.repository

import com.jcjiron.androidsample.data.local.LocalDataSource
import com.jcjiron.androidsample.data.local.toDomain
import com.jcjiron.androidsample.data.remote.RemoteDataSource
import com.jcjiron.androidsample.data.remote.toEntity
import com.jcjiron.androidsample.domain.model.Character
import com.jcjiron.androidsample.domain.repository.CharacterRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
) : CharacterRepository {

    override fun observeCharacters(): Flow<List<Character>> =
        localDataSource.observeCharacters().map { entities -> entities.map { it.toDomain() } }

    override suspend fun fetchPage(page: Int): Result<Boolean> = try {
        val response = remoteDataSource.getCharacters(page)
        val entities = response.results.map { it.toEntity() }
        if (page == FIRST_PAGE) {
            localDataSource.replaceCharacters(entities)
        } else {
            localDataSource.saveCharacters(entities)
        }
        Result.success(response.info.next != null)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

    companion object {
        const val FIRST_PAGE = 1
    }
}
