package com.jcjiron.androidsample.datakmp.repository

import com.jcjiron.androidsample.core.domain.model.Character
import com.jcjiron.androidsample.core.domain.repository.CharacterRepository
import com.jcjiron.androidsample.datakmp.local.LocalDataSource
import com.jcjiron.androidsample.datakmp.local.toDomain
import com.jcjiron.androidsample.datakmp.remote.RemoteDataSource
import com.jcjiron.androidsample.datakmp.remote.toEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** La misma interfaz de :core que implementa :data-android, ahora con librerías multiplataforma. */
class CharacterRepositoryImpl(
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
