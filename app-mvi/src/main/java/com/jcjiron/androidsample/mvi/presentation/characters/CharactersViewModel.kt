package com.jcjiron.androidsample.mvi.presentation.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jcjiron.androidsample.core.domain.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * En MVI el ViewModel tiene UNA entrada ([onIntent]) y UNA salida ([state]).
 * No decide cómo cambia el estado: eso lo hace [CharactersReducer].
 */
@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val repository: CharacterRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CharactersState(isRefreshing = true))
    val state: StateFlow<CharactersState> = _state.asStateFlow()

    private var loadJob: Job? = null

    init {
        repository.observeCharacters()
            .onEach { dispatch(CharactersResult.CharactersChanged(it)) }
            .launchIn(viewModelScope)
        onIntent(CharactersIntent.Refresh)
    }

    fun onIntent(intent: CharactersIntent) {
        when (intent) {
            CharactersIntent.Refresh -> loadPage(FIRST_PAGE)
            CharactersIntent.LoadMore -> {
                val current = _state.value
                if (loadJob?.isActive != true && !current.endReached) loadPage(current.nextPage)
            }
            CharactersIntent.ErrorShown -> dispatch(CharactersResult.ErrorCleared)
        }
    }

    private fun loadPage(page: Int) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            dispatch(
                if (page == FIRST_PAGE) CharactersResult.RefreshStarted else CharactersResult.LoadMoreStarted,
            )
            repository.fetchPage(page)
                .onSuccess { hasMore -> dispatch(CharactersResult.PageLoaded(page, hasMore)) }
                .onFailure { dispatch(CharactersResult.LoadFailed) }
        }
    }

    private fun dispatch(result: CharactersResult) {
        _state.update { CharactersReducer.reduce(it, result) }
    }

    private companion object {
        const val FIRST_PAGE = 1
    }
}
