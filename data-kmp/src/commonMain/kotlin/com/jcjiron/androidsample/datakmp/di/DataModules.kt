package com.jcjiron.androidsample.datakmp.di

import com.jcjiron.androidsample.core.AppConfig
import org.koin.core.module.Module
import org.koin.dsl.module

/** Todo lo que una app necesita cargar en Koin para tener datos. */
fun dataModules(config: AppConfig): List<Module> = listOf(
    module { single { config } },
    platformModule,
    networkModule,
    databaseModule,
    repositoryModule,
)
