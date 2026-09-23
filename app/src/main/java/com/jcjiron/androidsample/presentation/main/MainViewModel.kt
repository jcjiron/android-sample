package com.jcjiron.androidsample.presentation.main

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jcjiron.androidsample.R
import com.jcjiron.androidsample.domain.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: CharacterRepository,
) : ViewModel() {

    private val loadState = MutableStateFlow(LoadState(isRefreshing = true))
    private var loadJob: Job? = null
    private var nextPage = FIRST_PAGE
    private var endReached = false

    val uiState: StateFlow<MainUiState> =
        combine(repository.observeCharacters(), loadState) { characters, load ->
            MainUiState(
                characters = characters,
                isRefreshing = load.isRefreshing,
                isLoadingMore = load.isLoadingMore,
                errorMessage = load.errorMessage,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = MainUiState(isRefreshing = true),
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
                .onFailure { showError(R.string.error_network) }
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
                .onFailure { showError(R.string.error_network) }
            loadState.update { it.copy(isLoadingMore = false) }
        }
    }

    fun onErrorShown() {
        loadState.update { it.copy(errorMessage = null) }
    }

    private fun showError(@StringRes message: Int) {
        loadState.update { it.copy(errorMessage = message) }
    }

    private data class LoadState(
        val isRefreshing: Boolean = false,
        val isLoadingMore: Boolean = false,
        @StringRes val errorMessage: Int? = null,
    )

    private companion object {
        const val FIRST_PAGE = 1
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
