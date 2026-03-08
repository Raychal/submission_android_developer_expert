package com.raychal.favorite.ui

import com.raychal.core.domain.model.Game
import com.raychal.core.ui.base.UiIntent
import com.raychal.core.ui.base.UiState

data class FavoriteState(
    val isLoading: Boolean = false,
    val favorites: List<Game> = emptyList()
) : UiState

sealed class FavoriteIntent : UiIntent {
    data object GetFavorites : FavoriteIntent()
}
