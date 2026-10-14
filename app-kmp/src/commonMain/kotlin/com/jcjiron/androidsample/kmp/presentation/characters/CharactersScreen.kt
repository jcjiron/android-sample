package com.jcjiron.androidsample.kmp.presentation.characters

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.jcjiron.androidsample.ui.characters.CharactersContent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CharactersScreen(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: CharactersViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    CharactersContent(
        characters = state.characters,
        isRefreshing = state.isRefreshing,
        isLoadingMore = state.isLoadingMore,
        errorMessage = state.errorMessage,
        snackbarHostState = snackbarHostState,
        onRefresh = viewModel::refresh,
        onLoadMore = viewModel::loadMore,
        onErrorShown = viewModel::onErrorShown,
        modifier = modifier,
    )
}
