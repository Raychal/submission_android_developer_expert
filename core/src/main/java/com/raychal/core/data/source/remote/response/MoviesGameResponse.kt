package com.raychal.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class MoviesGameResponse(
    @field:SerializedName("count")
    val count: Int,
    @field:SerializedName("next")
    val next: String? = null,
    @field:SerializedName("previous")
    val previous: String? = null,
    @field:SerializedName("results")
    val results: List<MovieResponse>?
) {
    data class MovieResponse(
        @field:SerializedName("name")
        val name: String,
        @field:SerializedName("preview")
        val preview: String,
        @field:SerializedName("data")
        val data: DataMovieResponse
    ) {
        data class DataMovieResponse(
            @field:SerializedName("max")
            val max: String
        )
    }

}
