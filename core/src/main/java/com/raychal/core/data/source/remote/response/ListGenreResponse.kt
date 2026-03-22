package com.raychal.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class ListGenreResponse(
    @SerializedName("results")
    val results: List<GenreResponse>
) {
    data class GenreResponse(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String
    )
}
