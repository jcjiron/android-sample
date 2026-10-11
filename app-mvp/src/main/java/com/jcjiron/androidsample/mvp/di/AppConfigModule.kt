package com.jcjiron.androidsample.mvp.di

import com.jcjiron.androidsample.mvp.BuildConfig
import com.jcjiron.androidsample.core.AppConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Le pasa a `:core` la URL y el modo debug que vienen del build type de ESTA app. */
@Module
@InstallIn(SingletonComponent::class)
object AppConfigModule {

    @Provides
    @Singleton
    fun provideAppConfig() = AppConfig(
        baseUrl = BuildConfig.BASE_URL,
        isDebug = BuildConfig.DEBUG,
    )
}
