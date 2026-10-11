package com.jcjiron.androidsample.mvp.presentation.characters

import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.jcjiron.androidsample.core.domain.model.Character
import com.jcjiron.androidsample.core.presentation.characters.CharactersContent

/**
 * Implementación de la View del contrato para Compose.
 * Cada `show…` del Presenter guarda el dato en un estado de Compose y la pantalla se repinta.
 */
class CharactersComposeView : CharactersContract.View {

    var characters by mutableStateOf(emptyList<Character>())
        private set
    var isRefreshing by mutableStateOf(false)
        private set
    var isLoadingMore by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<Int?>(null)
        private set

    override fun showCharacters(characters: List<Character>) {
        this.characters = characters
    }

    override fun showRefreshing(isRefreshing: Boolean) {
        this.isRefreshing = isRefreshing
    }

    override fun showLoadingMore(isLoadingMore: Boolean) {
        this.isLoadingMore = isLoadingMore
    }

    override fun showError(@StringRes message: Int) {
        errorMessage = message
    }

    fun clearError() {
        errorMessage = null
    }
}

/** MVP: la pantalla se "engancha" al Presenter al aparecer y se suelta al desaparecer. */
@Composable
fun CharactersScreen(
    presenter: CharactersContract.Presenter,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val view = remember { CharactersComposeView() }

    DisposableEffect(presenter) {
        presenter.attachView(view)
        onDispose { presenter.detachView() }
    }

    CharactersContent(
        characters = view.characters,
        isRefreshing = view.isRefreshing,
        isLoadingMore = view.isLoadingMore,
        errorMessage = view.errorMessage,
        snackbarHostState = snackbarHostState,
        onRefresh = presenter::onRefresh,
        onLoadMore = presenter::onLoadMore,
        onErrorShown = view::clearError,
        modifier = modifier,
    )
}
