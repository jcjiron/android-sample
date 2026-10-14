package com.jcjiron.androidsample.mvi.presentation.characters

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jcjiron.androidsample.ui.characters.CharactersContent

/** MVI: la pantalla pinta UN state y todo lo que hace el usuario se manda como Intent. */
@Composable
fun CharactersScreen(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: CharactersViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CharactersContent(
        characters = state.characters,
        isRefreshing = state.isRefreshing,
        isLoadingMore = state.isLoadingMore,
        errorMessage = state.errorMessage,
        snackbarHostState = snackbarHostState,
        onRefresh = { viewModel.onIntent(CharactersIntent.Refresh) },
        onLoadMore = { viewModel.onIntent(CharactersIntent.LoadMore) },
        onErrorShown = { viewModel.onIntent(CharactersIntent.ErrorShown) },
        modifier = modifier,
    )
}
