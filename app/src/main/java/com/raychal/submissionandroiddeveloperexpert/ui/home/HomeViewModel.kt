package com.raychal.submissionandroiddeveloperexpert.ui.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.raychal.core.data.Resource
import com.raychal.core.domain.model.Game
import com.raychal.core.domain.usecase.GameUseCase
import com.raychal.core.ui.base.BaseViewModel
import com.raychal.core.utils.network.NetworkObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    private val gameUseCase: GameUseCase,
    networkObserver: NetworkObserver
) :
    BaseViewModel<HomeState, HomeIntent>(HomeState(), networkObserver) {

    private val _searchQuery = MutableStateFlow("")
    private val _genreFilter = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val gamesPagingData: Flow<PagingData<Game>> = combine(
        _searchQuery.debounce(500),
        _genreFilter
    ) { query, genre ->
        query to genre
    }.flatMapLatest { (query, genre) ->
        gameUseCase.getAllGame(
            search = query.ifEmpty { null },
            genres = genre.ifEmpty { null }
        )
    }.cachedIn(viewModelScope)

    init {
        handleIntent(HomeIntent.GetGenres)
    }

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.SearchGames -> {
                _searchQuery.value = intent.query
                setState { copy(search = intent.query) }
            }
            is HomeIntent.SelectGenre -> {
                val newGenre = intent.genreSlug
                _genreFilter.value = newGenre
                setState { copy(selectedGenre = newGenre) }
            }
            is HomeIntent.GetGenres -> {
                viewModelScope.launch {
                    gameUseCase.getGenres().collect { resource ->
                        if (resource is Resource.Success) {
                            setState { copy(genres = resource.data ?: emptyList()) }
                        }
                    }
                }
            }
            is HomeIntent.Refresh -> {
                _searchQuery.value = ""
                _genreFilter.value = ""
                setState {
                    copy(
                        search = "",
                        selectedGenre = ""
                    )
                }
            }
        }
    }
}
