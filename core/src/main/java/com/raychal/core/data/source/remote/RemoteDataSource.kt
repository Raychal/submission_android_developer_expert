package com.raychal.core.data.source.remote

import com.raychal.core.data.source.remote.network.ApiService
import com.raychal.core.data.source.remote.response.GameResponse
import com.raychal.core.data.source.remote.response.ListGameResponse

class RemoteDataSource(private val apiService: ApiService) {
    suspend fun getGames(page: Int, pageSize: Int, search: String?): ListGameResponse {
        return apiService.getGames(page, pageSize, search)
    }

    suspend fun getDetailGame(id: Int): GameResponse {
        return apiService.getDetailGame(id)
    }
}
