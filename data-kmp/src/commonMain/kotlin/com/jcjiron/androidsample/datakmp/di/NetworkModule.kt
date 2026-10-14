package com.jcjiron.androidsample.datakmp.di

import com.jcjiron.androidsample.core.AppConfig
import com.jcjiron.androidsample.datakmp.remote.RemoteDataSource
import com.jcjiron.androidsample.datakmp.remote.RickAndMortyApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

/** Koin: `single { }` es el equivalente a `@Provides @Singleton` de Hilt. */
val networkModule = module {
    single { Json { ignoreUnknownKeys = true } }
    single {
        val config = get<AppConfig>()
        val serializer = get<Json>()
        // El motor (OkHttp en Android, Darwin en iOS) lo da el platformModule.
        HttpClient(get<HttpClientEngine>()) {
            install(ContentNegotiation) { json(serializer) }
            defaultRequest { url(config.baseUrl) }
        }
    }
    single { RickAndMortyApi(get()) }
    single { RemoteDataSource(get()) }
}
