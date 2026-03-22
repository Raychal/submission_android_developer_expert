package com.raychal.core.data.source.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.raychal.core.data.source.remote.network.ApiService
import com.raychal.core.domain.model.Game
import com.raychal.core.utils.DataMapper

class GamePagingSource(
    private val apiService: ApiService,
    private val search: String?,
    private val genres: String?
) : PagingSource<Int, Game>() {

    override fun getRefreshKey(state: PagingState<Int, Game>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Game> {
        return try {
            val position = params.key ?: 1
            val response = apiService.getGames(
                page = position,
                pageSize = params.loadSize,
                search = search,
                genres = genres
            )
            val games = response.results.map { DataMapper.mapResponseToDomain(it) }

            LoadResult.Page(
                data = games,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (response.next == null || games.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}
