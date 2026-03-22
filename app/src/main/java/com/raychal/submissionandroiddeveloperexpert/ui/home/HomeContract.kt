package com.raychal.submissionandroiddeveloperexpert.ui.home

import androidx.paging.PagingData
import com.raychal.core.domain.model.Game
import com.raychal.core.domain.model.Genre
import com.raychal.core.ui.base.UiIntent
import com.raychal.core.ui.base.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class HomeState(
    val pagingData: Flow<PagingData<Game>> = emptyFlow(),
    val search: String = "",
    val genres: List<Genre> = emptyList(),
    val selectedGenre: String = ""
) : UiState

sealed class HomeIntent : UiIntent {
    data class SearchGames(val query: String) : HomeIntent()
    data class SelectGenre(val genreSlug: String) : HomeIntent()
    object GetGenres : HomeIntent()
    class Refresh : HomeIntent()
}
