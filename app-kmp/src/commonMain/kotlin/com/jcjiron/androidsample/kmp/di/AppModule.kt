package com.jcjiron.androidsample.kmp.di

import com.jcjiron.androidsample.core.AppConfig
import com.jcjiron.androidsample.datakmp.di.dataModules
import com.jcjiron.androidsample.kmp.presentation.characters.CharactersViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {
    viewModel { CharactersViewModel(get()) }
}

/** Arranca Koin. Android lo llama desde KmpApp e iOS desde iOSApp.swift. */
fun startAppKoin(config: AppConfig, platform: KoinAppDeclaration = {}) {
    startKoin {
        platform()
        modules(dataModules(config) + appModule)
    }
}
