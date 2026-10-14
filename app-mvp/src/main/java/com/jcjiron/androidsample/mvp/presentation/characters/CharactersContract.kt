package com.jcjiron.androidsample.mvp.presentation.characters

import com.jcjiron.androidsample.core.domain.model.Character
import org.jetbrains.compose.resources.StringResource

/**
 * MVP = Model-View-Presenter.
 *
 * El contrato define cómo platican la View y el Presenter:
 * - La **View** es "tonta": solo sabe mostrar lo que le dicen (`show…`).
 * - El **Presenter** recibe los eventos de la View (`on…`), habla con el repositorio
 *   y le ordena a la View qué mostrar. Tiene una referencia a la View (a diferencia de MVVM).
 */
interface CharactersContract {

    interface View {
        fun showCharacters(characters: List<Character>)
        fun showRefreshing(isRefreshing: Boolean)
        fun showLoadingMore(isLoadingMore: Boolean)
        fun showError(message: StringResource)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun onRefresh()
        fun onLoadMore()
    }
}
