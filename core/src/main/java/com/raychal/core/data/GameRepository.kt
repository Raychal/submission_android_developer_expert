package com.raychal.core.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.raychal.core.data.source.local.room.GameDao
import com.raychal.core.data.source.remote.network.ApiService
import com.raychal.core.data.source.remote.paging.GamePagingSource
import com.raychal.core.domain.model.Game
import com.raychal.core.domain.repository.IGameRepository
import com.raychal.core.utils.DataMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.io.IOException

class GameRepository(
    private val apiService: ApiService,
    private val gameDao: GameDao
) : IGameRepository {

    override fun getAllGame(search: String?): Flow<PagingData<Game>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                GamePagingSource(apiService, search)
            }
        ).flow
    }

    override fun getDetailGame(id: Int): Flow<Resource<Game>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getDetailGame(id)
            val domainData = DataMapper.mapResponseToDomain(response)

            val localGame = gameDao.getGameById(id).first()
            val finalData = domainData.copy(isFavorite = localGame?.isFavorite ?: false)

            emit(Resource.Success(finalData))
        } catch (e: Exception) {
            if (e is IOException) {
                val localGame = gameDao.getGameById(id).first()
                if (localGame != null) {
                    emit(Resource.Success(DataMapper.mapEntitiesToDomain(listOf(localGame)).first()))
                } else {
                    emit(Resource.Error("Offline: Data not found locally.", null))
                }
            } else {
                emit(Resource.Error(e.message ?: "Unknown Error"))
            }
        }
    }.flowOn(Dispatchers.IO) as Flow<Resource<Game>>

    override fun getFavoriteGame(): Flow<List<Game>> {
        return gameDao.getFavoriteGame().map {
            DataMapper.mapEntitiesToDomain(it)
        }
    }

    override suspend fun setFavoriteGame(game: Game, state: Boolean) {
        val gameEntity = DataMapper.mapDomainToEntity(game)
        gameEntity.isFavorite = state
        withContext(Dispatchers.IO) {
            gameDao.insertSingleGame(gameEntity)
        }
    }
}
