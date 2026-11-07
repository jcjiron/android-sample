package com.jcjiron.androidsample.core.data.repository

import com.jcjiron.androidsample.core.data.local.LocalDataSource
import com.jcjiron.androidsample.core.data.local.toDomain
import com.jcjiron.androidsample.core.data.remote.CharacterPageDto
import com.jcjiron.androidsample.core.data.remote.RemoteDataSource
import com.jcjiron.androidsample.core.data.remote.toEntity
import com.jcjiron.androidsample.core.domain.model.Character
import com.jcjiron.androidsample.core.domain.repository.CharacterRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
) : CharacterRepository {

    override fun observeCharacters(): Flow<List<Character>> =
        localDataSource.observeCharacters().map { entities -> entities.map { it.toDomain() } }

    override fun observeCharacter(id: Int): Flow<Character?> =
        localDataSource.observeCharacter(id).map { it?.toDomain() }

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

    override suspend fun downloadAll(
        onProgress: suspend (page: Int, totalPages: Int) -> Unit,
    ): Result<Int> = try {
        var page = FIRST_PAGE
        do {
            val response = fetchWithRetry(page)
            val entities = response.results.map { it.toEntity() }
            if (page == FIRST_PAGE) {
                localDataSource.replaceCharacters(entities)
            } else {
                localDataSource.saveCharacters(entities)
            }
            onProgress(page, response.info.pages)
            page++
            // Pausa corta entre páginas para no saturar una API pública y gratuita.
            if (response.info.next != null) delay(PAGE_DELAY_MILLIS)
        } while (response.info.next != null)
        Result.success(localDataSource.count())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Reintenta una página hasta [MAX_ATTEMPTS] veces. Si el servidor responde 429 (demasiadas
     * peticiones) se espera lo que pida en `Retry-After`; si no lo dice, 2 s, 4 s, 8 s…
     */
    private suspend fun fetchWithRetry(page: Int): CharacterPageDto {
        var attempt = 1
        while (true) {
            try {
                return remoteDataSource.getCharacters(page)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (attempt >= MAX_ATTEMPTS) throw e
                val backoff = RETRY_BASE_DELAY_MILLIS shl (attempt - 1)
                delay(retryAfterMillis(e) ?: backoff)
                attempt++
            }
        }
    }

    private fun retryAfterMillis(error: Exception): Long? {
        if (error !is HttpException || error.code() != HTTP_TOO_MANY_REQUESTS) return null
        val seconds = error.response()?.headers()?.get("Retry-After")?.toLongOrNull() ?: return null
        return (seconds * 1_000).coerceAtMost(MAX_RETRY_AFTER_MILLIS)
    }

    companion object {
        const val FIRST_PAGE = 1
        private const val MAX_ATTEMPTS = 5
        private const val RETRY_BASE_DELAY_MILLIS = 2_000L
        private const val MAX_RETRY_AFTER_MILLIS = 30_000L
        private const val PAGE_DELAY_MILLIS = 300L
        private const val HTTP_TOO_MANY_REQUESTS = 429
    }
}
