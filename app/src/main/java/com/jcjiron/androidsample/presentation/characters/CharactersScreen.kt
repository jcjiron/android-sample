package com.jcjiron.androidsample.presentation.characters

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.jcjiron.androidsample.core.presentation.characters.CharactersContent

/** MVVM: la pantalla observa los LiveData del ViewModel y le avisa de los eventos. */
@Composable
fun CharactersScreen(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: CharactersViewModel = hiltViewModel(),
) {
    // observeAsState convierte cada LiveData en estado de Compose.
    val characters by viewModel.characters.observeAsState(emptyList())
    val isRefreshing by viewModel.isRefreshing.observeAsState(false)
    val isLoadingMore by viewModel.isLoadingMore.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()

    CharactersContent(
        characters = characters,
        isRefreshing = isRefreshing,
        isLoadingMore = isLoadingMore,
        errorMessage = errorMessage,
        snackbarHostState = snackbarHostState,
        onRefresh = viewModel::refresh,
        onLoadMore = viewModel::loadMore,
        onErrorShown = viewModel::onErrorShown,
        modifier = modifier,
    )
}
