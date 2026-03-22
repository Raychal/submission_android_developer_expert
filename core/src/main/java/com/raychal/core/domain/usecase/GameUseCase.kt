package com.raychal.core.domain.usecase

import androidx.paging.PagingData
import com.raychal.core.data.Resource
import com.raychal.core.domain.model.Game
import com.raychal.core.domain.model.Genre
import kotlinx.coroutines.flow.Flow

interface GameUseCase {
    fun getAllGame(search: String? = null, genres: String? = null): Flow<PagingData<Game>>
    fun getGenres(): Flow<Resource<List<Genre>>>
    fun getDetailGame(id: Int): Flow<Resource<Game>>
    fun getFavoriteGame(): Flow<List<Game>>
    suspend fun setFavoriteGame(game: Game, state: Boolean)
}
