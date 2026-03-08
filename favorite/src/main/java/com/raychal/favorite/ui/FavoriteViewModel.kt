package com.raychal.favorite.ui

import androidx.lifecycle.viewModelScope
import com.raychal.core.domain.usecase.GameUseCase
import com.raychal.core.ui.base.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FavoriteViewModel(private val gameUseCase: GameUseCase) :
    BaseViewModel<FavoriteState, FavoriteIntent>(FavoriteState()) {

    override fun handleIntent(intent: FavoriteIntent) {
        when (intent) {
            is FavoriteIntent.GetFavorites -> getFavorites()
        }
    }

    private fun getFavorites() {
        gameUseCase.getFavoriteGame().onEach { list ->
            setState { copy(isLoading = false, favorites = list) }
        }.launchIn(viewModelScope)
    }
}
