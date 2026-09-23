package com.jcjiron.androidsample.presentation.characters

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.jcjiron.androidsample.R
import com.jcjiron.androidsample.domain.model.Character
import com.jcjiron.androidsample.domain.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel de la pantalla de personajes.
 *
 * Patrón clásico: cada dato tiene un `MutableLiveData` privado (solo el ViewModel lo cambia)
 * y un `LiveData` público (la UI solo lo lee y lo observa).
 */
@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val repository: CharacterRepository,
) : ViewModel() {

    // Room avisa cada vez que cambian los datos; lo convertimos a LiveData para la UI.
    val characters: LiveData<List<Character>> = repository.observeCharacters().asLiveData()

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _isLoadingMore = MutableLiveData(false)
    val isLoadingMore: LiveData<Boolean> = _isLoadingMore

    private val _errorMessage = MutableLiveData<Int?>(null)
    val errorMessage: LiveData<Int?> = _errorMessage

    private var loadJob: Job? = null
    private var nextPage = FIRST_PAGE
    private var endReached = false

    init {
        refresh()
    }

    fun refresh() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _isLoadingMore.value = false
            _isRefreshing.value = true
            repository.fetchPage(FIRST_PAGE)
                .onSuccess { hasMore ->
                    nextPage = FIRST_PAGE + 1
                    endReached = !hasMore
                }
                .onFailure { showError(R.string.error_network) }
            _isRefreshing.value = false
        }
    }

    fun loadMore() {
        if (loadJob?.isActive == true || endReached) return
        loadJob = viewModelScope.launch {
            _isLoadingMore.value = true
            repository.fetchPage(nextPage)
                .onSuccess { hasMore ->
                    nextPage++
                    endReached = !hasMore
                }
                .onFailure { showError(R.string.error_network) }
            _isLoadingMore.value = false
        }
    }

    fun onErrorShown() {
        _errorMessage.value = null
    }

    private fun showError(@StringRes message: Int) {
        _errorMessage.value = message
    }

    private companion object {
        const val FIRST_PAGE = 1
    }
}
