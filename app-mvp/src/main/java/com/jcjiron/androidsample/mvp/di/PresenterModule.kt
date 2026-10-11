package com.jcjiron.androidsample.mvp.di

import com.jcjiron.androidsample.mvp.presentation.characters.CharactersContract
import com.jcjiron.androidsample.mvp.presentation.characters.CharactersPresenter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

/** Los Presenters viven lo que vive la Activity (no sobreviven a rotar la pantalla). */
@Module
@InstallIn(ActivityComponent::class)
abstract class PresenterModule {

    @Binds
    abstract fun bindCharactersPresenter(impl: CharactersPresenter): CharactersContract.Presenter
}
