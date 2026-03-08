package com.raychal.submissionandroiddeveloperexpert.ui.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.raychal.core.domain.model.Game
import com.raychal.core.domain.usecase.GameUseCase
import com.raychal.core.ui.base.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

class HomeViewModel(private val gameUseCase: GameUseCase) :
    BaseViewModel<HomeState, HomeIntent>(HomeState()) {

    private val _searchQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val gamesPagingData: Flow<PagingData<Game>> = _searchQuery
        .debounce(500)
        .flatMapLatest { query ->
            gameUseCase.getAllGame(query.ifEmpty { null })
        }
        .cachedIn(viewModelScope)

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.SearchGames -> {
                _searchQuery.value = intent.query
                setState { copy(search = intent.query) }
            }
            is HomeIntent.Refresh -> Unit
        }
    }
}
