package com.jcjiron.androidsample.mvi.presentation.characters

import com.jcjiron.androidsample.ui.resources.Res
import com.jcjiron.androidsample.ui.resources.error_network

/**
 * Función pura: mismo State + mismo Result = mismo State nuevo. Sin coroutines, sin red, sin Android.
 * Por eso es lo más fácil de probar en MVI.
 */
object CharactersReducer {

    fun reduce(state: CharactersState, result: CharactersResult): CharactersState = when (result) {
        is CharactersResult.CharactersChanged -> state.copy(characters = result.characters)

        CharactersResult.RefreshStarted -> state.copy(isRefreshing = true, isLoadingMore = false)

        CharactersResult.LoadMoreStarted -> state.copy(isLoadingMore = true)

        is CharactersResult.PageLoaded -> state.copy(
            isRefreshing = false,
            isLoadingMore = false,
            nextPage = result.page + 1,
            endReached = !result.hasMore,
        )

        CharactersResult.LoadFailed -> state.copy(
            isRefreshing = false,
            isLoadingMore = false,
            errorMessage = Res.string.error_network,
        )

        CharactersResult.ErrorCleared -> state.copy(errorMessage = null)
    }
}
