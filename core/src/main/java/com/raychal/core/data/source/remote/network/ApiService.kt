package com.raychal.core.data.source.remote.network

import com.raychal.core.data.source.remote.response.GameResponse
import com.raychal.core.data.source.remote.response.ListGameResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("games")
    suspend fun getGames(
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null,
        @Query("search") search: String? = null
    ): ListGameResponse

    @GET("games/{id}")
    suspend fun getDetailGame(
        @Path("id") id: Int
    ): GameResponse
}
