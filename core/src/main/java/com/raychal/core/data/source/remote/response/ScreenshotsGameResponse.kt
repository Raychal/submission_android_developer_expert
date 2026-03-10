package com.raychal.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class ScreenshotsGameResponse(
    @field:SerializedName("count")
    val count: Int,
    @field:SerializedName("next")
    val next: String? = null,
    @field:SerializedName("previous")
    val previous: String? = null,
    @field:SerializedName("results")
    val results: List<ScreenshotResponse>?
) {
    data class ScreenshotResponse(
        @field:SerializedName("image")
        val image: String
    )
}
