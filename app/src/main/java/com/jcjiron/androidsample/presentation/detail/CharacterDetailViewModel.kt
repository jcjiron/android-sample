package com.jcjiron.androidsample.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.jcjiron.androidsample.core.domain.model.Character
import com.jcjiron.androidsample.core.domain.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * El id llega en el Intent de la Activity; SavedStateHandle lo recibe solo
 * (y lo conserva si Android mata y recrea la pantalla).
 */
@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: CharacterRepository,
) : ViewModel() {

    private val characterId: Int =
        checkNotNull(savedStateHandle[CharacterDetailActivity.EXTRA_CHARACTER_ID])

    val character: LiveData<Character?> = repository.observeCharacter(characterId).asLiveData()
}
