package com.raychal.submissionandroiddeveloperexpert.ui.detail

import com.raychal.core.domain.model.Game
import com.raychal.core.ui.base.UiIntent
import com.raychal.core.ui.base.UiState

data class DetailState(
    val isLoading: Boolean = false,
    val game: Game? = null,
    val errorMessage: String? = null
) : UiState

sealed class DetailIntent : UiIntent {
    data class GetDetail(val id: Int) : DetailIntent()
    data class ToggleFavorite(val game: Game) : DetailIntent()
}
