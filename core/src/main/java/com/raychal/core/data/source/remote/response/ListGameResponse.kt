package com.raychal.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class ListGameResponse(
    @field:SerializedName("count")
    val count: Int,

    @field:SerializedName("next")
    val next: String?,

    @field:SerializedName("results")
    val results: List<GameResponse>
)
