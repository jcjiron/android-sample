package com.jcjiron.androidsample.mvi.presentation.characters

import com.jcjiron.androidsample.core.domain.model.Character
import org.jetbrains.compose.resources.StringResource

/**
 * MVI = Model-View-Intent.
 *
 * - **State**: UN solo objeto inmutable con todo lo que la pantalla necesita.
 * - **Intent**: lo que el usuario QUIERE hacer. Es la única entrada al ViewModel.
 * - **Result**: lo que pasó al procesar un Intent (o al llegar datos).
 * - **Reducer**: función pura `(State, Result) -> State`. Es el único lugar donde cambia el estado.
 *
 * Flujo en un solo sentido:  View --Intent--> ViewModel --Result--> Reducer --State--> View
 */
data class CharactersState(
    val characters: List<Character> = emptyList(),
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: StringResource? = null,
    val nextPage: Int = 1,
    val endReached: Boolean = false,
)

sealed interface CharactersIntent {
    data object Refresh : CharactersIntent
    data object LoadMore : CharactersIntent
    data object ErrorShown : CharactersIntent
}

sealed interface CharactersResult {
    data class CharactersChanged(val characters: List<Character>) : CharactersResult
    data object RefreshStarted : CharactersResult
    data object LoadMoreStarted : CharactersResult
    data class PageLoaded(val page: Int, val hasMore: Boolean) : CharactersResult
    data object LoadFailed : CharactersResult
    data object ErrorCleared : CharactersResult
}
