package com.jcjiron.androidsample.datakmp.di

import com.jcjiron.androidsample.core.domain.repository.CharacterRepository
import com.jcjiron.androidsample.datakmp.repository.CharacterRepositoryImpl
import org.koin.dsl.module

/** Equivalente al `@Binds` de Hilt: cuando pidan la interfaz, se da esta implementación. */
val repositoryModule = module {
    single<CharacterRepository> { CharacterRepositoryImpl(get(), get()) }
}
