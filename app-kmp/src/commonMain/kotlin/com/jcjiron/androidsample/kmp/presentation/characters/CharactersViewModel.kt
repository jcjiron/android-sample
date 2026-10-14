package com.jcjiron.androidsample.kmp.presentation.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jcjiron.androidsample.core.domain.model.Character
import com.jcjiron.androidsample.core.domain.repository.CharacterRepository
import com.jcjiron.androidsample.ui.resources.Res
import com.jcjiron.androidsample.ui.resources.error_network
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

data class CharactersUiState(
    val characters: List<Character> = emptyList(),
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: StringResource? = null,
)

/**
 * MVVM multiplataforma: el mismo ViewModel corre en Android y en iOS.
 * LiveData no existe en iOS, por eso aquí el estado sale con StateFlow.
 */
class CharactersViewModel(
    private val repository: CharacterRepository,
) : ViewModel() {

    private val loadState = MutableStateFlow(LoadState(isRefreshing = true))
    private var loadJob: Job? = null
    private var nextPage = FIRST_PAGE
    private var endReached = false

    val uiState: StateFlow<CharactersUiState> =
        combine(repository.observeCharacters(), loadState) { characters, load ->
            CharactersUiState(
                characters = characters,
                isRefreshing = load.isRefreshing,
                isLoadingMore = load.isLoadingMore,
                errorMessage = load.errorMessage,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = CharactersUiState(isRefreshing = true),
        )

    init {
        refresh()
    }

    fun refresh() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            loadState.update { it.copy(isRefreshing = true, isLoadingMore = false) }
            repository.fetchPage(FIRST_PAGE)
                .onSuccess { hasMore ->
                    nextPage = FIRST_PAGE + 1
                    endReached = !hasMore
                }
                .onFailure { loadState.update { it.copy(errorMessage = Res.string.error_network) } }
            loadState.update { it.copy(isRefreshing = false) }
        }
    }

    fun loadMore() {
        if (loadJob?.isActive == true || endReached) return
        loadJob = viewModelScope.launch {
            loadState.update { it.copy(isLoadingMore = true) }
            repository.fetchPage(nextPage)
                .onSuccess { hasMore ->
                    nextPage++
                    endReached = !hasMore
                }
                .onFailure { loadState.update { it.copy(errorMessage = Res.string.error_network) } }
            loadState.update { it.copy(isLoadingMore = false) }
        }
    }

    fun onErrorShown() {
        loadState.update { it.copy(errorMessage = null) }
    }

    private data class LoadState(
        val isRefreshing: Boolean = false,
        val isLoadingMore: Boolean = false,
        val errorMessage: StringResource? = null,
    )

    private companion object {
        const val FIRST_PAGE = 1
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
