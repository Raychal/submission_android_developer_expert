package com.raychal.core.data.source.remote.network

import com.raychal.core.data.source.remote.response.GameResponse
import com.raychal.core.data.source.remote.response.ListGameResponse
import com.raychal.core.data.source.remote.response.ListGenreResponse
import com.raychal.core.data.source.remote.response.MoviesGameResponse
import com.raychal.core.data.source.remote.response.ScreenshotsGameResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("games")
    suspend fun getGames(
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null,
        @Query("search") search: String? = null,
        @Query("genres") genres: String? = null
    ): ListGameResponse

    @GET("genres")
    suspend fun getGenres(): ListGenreResponse

    @GET("games/{id}")
    suspend fun getDetailGame(
        @Path("id") id: Int
    ): GameResponse

    @GET("games/{id}/screenshots")
    suspend fun getDetailGameScreenshots(
        @Path("id") id: Int
    ): ScreenshotsGameResponse

    @GET("games/{id}/movies")
    suspend fun getDetailGameMovies(
        @Path("id") id: Int
    ): MoviesGameResponse
}
