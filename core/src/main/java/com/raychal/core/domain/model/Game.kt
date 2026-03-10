package com.raychal.core.domain.model

data class Game(
    val id: Int,
    val name: String,
    val released: String? = "N/A",
    val descriptionRaw: String? = null,
    val tba: Boolean,
    val metaScore: Int,
    val backgroundImage: String?,
    val rating: Double,
    val ratingTop: Int,
    val updated: String?,
    val genres: List<String>,
    val platforms: List<String>,
    val tags: List<String>,
    val esrbRating: String?,
    val screenshots: List<String>,
    val movies: List<GameMovie>,
    val isFavorite: Boolean = false
)
