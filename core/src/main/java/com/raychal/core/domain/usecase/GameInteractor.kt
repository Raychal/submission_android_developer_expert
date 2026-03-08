package com.raychal.core.domain.usecase

import androidx.paging.PagingData
import com.raychal.core.data.Resource
import com.raychal.core.domain.model.Game
import com.raychal.core.domain.repository.IGameRepository
import kotlinx.coroutines.flow.Flow

class GameInteractor(private val gameRepository: IGameRepository) : GameUseCase {
    override fun getAllGame(search: String?): Flow<PagingData<Game>> =
        gameRepository.getAllGame(search)

    override fun getDetailGame(id: Int): Flow<Resource<Game>> = gameRepository.getDetailGame(id)
    override fun getFavoriteGame(): Flow<List<Game>> = gameRepository.getFavoriteGame()
    override suspend fun setFavoriteGame(game: Game, state: Boolean) = gameRepository.setFavoriteGame(game, state)
}
