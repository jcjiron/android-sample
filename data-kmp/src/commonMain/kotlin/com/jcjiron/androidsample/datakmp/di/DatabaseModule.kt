package com.jcjiron.androidsample.datakmp.di

import com.jcjiron.androidsample.datakmp.local.AppDatabase
import com.jcjiron.androidsample.datakmp.local.LocalDataSource
import org.koin.dsl.module

/** La base de datos en sí la crea el platformModule (cada sistema la guarda en otro lado). */
val databaseModule = module {
    single { get<AppDatabase>().characterDao() }
    single { LocalDataSource(get()) }
}
