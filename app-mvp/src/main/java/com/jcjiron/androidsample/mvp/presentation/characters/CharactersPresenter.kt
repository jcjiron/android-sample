package com.jcjiron.androidsample.mvp.presentation.characters

import com.jcjiron.androidsample.core.domain.repository.CharacterRepository
import com.jcjiron.androidsample.ui.resources.Res
import com.jcjiron.androidsample.ui.resources.error_network
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * El Presenter NO es un ViewModel de Jetpack: es una clase normal.
 * Por eso él mismo maneja su ciclo de vida con [attachView] / [detachView]
 * y su propio CoroutineScope, que se cancela al soltar la View para no fugar memoria.
 */
class CharactersPresenter @Inject constructor(
    private val repository: CharacterRepository,
) : CharactersContract.Presenter {

    private var view: CharactersContract.View? = null
    private var scope: CoroutineScope? = null
    private var loadJob: Job? = null

    private var nextPage = FIRST_PAGE
    private var endReached = false
    private var hasLoaded = false

    override fun attachView(view: CharactersContract.View) {
        this.view = view
        scope = MainScope()
        scope?.launch {
            repository.observeCharacters().collect { characters -> view.showCharacters(characters) }
        }
        if (!hasLoaded) onRefresh()
    }

    override fun detachView() {
        scope?.cancel()
        scope = null
        view = null
    }

    override fun onRefresh() {
        loadJob?.cancel()
        loadJob = scope?.launch {
            view?.showLoadingMore(false)
            view?.showRefreshing(true)
            repository.fetchPage(FIRST_PAGE)
                .onSuccess { hasMore ->
                    hasLoaded = true
                    nextPage = FIRST_PAGE + 1
                    endReached = !hasMore
                }
                .onFailure { view?.showError(Res.string.error_network) }
            view?.showRefreshing(false)
        }
    }

    override fun onLoadMore() {
        if (loadJob?.isActive == true || endReached) return
        loadJob = scope?.launch {
            view?.showLoadingMore(true)
            repository.fetchPage(nextPage)
                .onSuccess { hasMore ->
                    nextPage++
                    endReached = !hasMore
                }
                .onFailure { view?.showError(Res.string.error_network) }
            view?.showLoadingMore(false)
        }
    }

    private companion object {
        const val FIRST_PAGE = 1
    }
}
