package com.raychal.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class GameResponse(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("released")
    val released: String?,
    @field:SerializedName("description_raw")
    val descriptionRaw: String? = null,
    @field:SerializedName("tba")
    val tba: Boolean,
    @field:SerializedName("metacritic")
    val metaScore: Int,
    @field:SerializedName("background_image")
    val backgroundImage: String?,
    @field:SerializedName("rating")
    val rating: Double,
    @field:SerializedName("rating_top")
    val ratingTop: Int,
    @field:SerializedName("updated")
    val updated: String?,
    @field:SerializedName("parent_platforms")
    val parentPlatforms: List<ParentPlatformResponse>?,
    @field:SerializedName("genres")
    val genres: List<GenreResponse>?,
    @field:SerializedName("esrb_rating")
    val esrbRating: EsrbRatingResponse?,
    @field:SerializedName("tags")
    val tags: List<TagResponse>?,
    @field:SerializedName("short_screenshots")
    val shortScreenshots: List<ScreenshotResponse>?
) {
    data class ParentPlatformResponse(
        @field:SerializedName("platform")
        val platform: PlatformResponse
    )

    data class PlatformResponse(
        @field:SerializedName("name")
        val name: String
    )

    data class GenreResponse(
        @field:SerializedName("name")
        val name: String
    )

    data class EsrbRatingResponse(
        @field:SerializedName("name")
        val name: String
    )

    data class TagResponse(
        @field:SerializedName("name")
        val name: String
    )

    data class ScreenshotResponse(
        @field:SerializedName("image")
        val image: String
    )
}
