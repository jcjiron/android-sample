package com.jcjiron.androidsample.presentation.main

import androidx.annotation.StringRes
import com.jcjiron.androidsample.domain.model.Character

data class MainUiState(
    val characters: List<Character> = emptyList(),
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    @StringRes val errorMessage: Int? = null,
)
