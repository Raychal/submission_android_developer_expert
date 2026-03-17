package com.raychal.submissionandroiddeveloperexpert.ui.detail

import androidx.lifecycle.viewModelScope
import com.raychal.core.data.Resource
import com.raychal.core.domain.usecase.GameUseCase
import com.raychal.core.ui.base.BaseViewModel
import com.raychal.core.utils.network.NetworkObserver
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DetailViewModel(
    private val gameUseCase: GameUseCase,
    networkObserver: NetworkObserver
) :
    BaseViewModel<DetailState, DetailIntent>(DetailState(), networkObserver) {

    override fun handleIntent(intent: DetailIntent) {
        when (intent) {
            is DetailIntent.GetDetail -> getDetail(intent.id)
            is DetailIntent.ToggleFavorite -> toggleFavorite(intent.game)
        }
    }

    private fun getDetail(id: Int) {
        gameUseCase.getDetailGame(id).onEach { resource ->
            when (resource) {
                is Resource.Loading -> setState { copy(isLoading = true, errorMessage = null) }
                is Resource.Success -> setState { copy(isLoading = false, game = resource.data) }
                is Resource.Error -> setState { copy(isLoading = false, errorMessage = resource.message) }
            }
        }.launchIn(viewModelScope)
    }

    private fun toggleFavorite(game: com.raychal.core.domain.model.Game) {
        viewModelScope.launch {
            val newState = !game.isFavorite
            gameUseCase.setFavoriteGame(game, newState)
            setState { copy(game = game.copy(isFavorite = newState)) }
        }
    }
}
