package com.raychal.core.utils

import com.raychal.core.data.source.local.entity.GameEntity
import com.raychal.core.data.source.remote.response.GameResponse
import com.raychal.core.domain.model.Game

object DataMapper {
    fun mapEntitiesToDomain(input: List<GameEntity>): List<Game> =
        input.map {
            Game(
                id = it.id,
                name = it.name,
                released = it.released,
                tba = it.tba,
                metaScore = it.metaScore,
                backgroundImage = it.backgroundImage,
                rating = it.rating,
                ratingTop = it.ratingTop,
                updated = it.updated,
                isFavorite = it.isFavorite,
                genres = it.genres,
                platforms = it.platforms,
                tags = it.tags,
                esrbRating = it.esrbRating,
                screenshots = it.screenshots
            )
        }

    fun mapDomainToEntity(input: Game) = GameEntity(
        id = input.id,
        name = input.name,
        released = input.released,
        tba = input.tba,
        metaScore = input.metaScore,
        backgroundImage = input.backgroundImage,
        rating = input.rating,
        ratingTop = input.ratingTop,
        updated = input.updated,
        isFavorite = input.isFavorite,
        genres = input.genres,
        platforms = input.platforms,
        tags = input.tags,
        esrbRating = input.esrbRating,
        screenshots = input.screenshots
    )

    fun mapResponseToDomain(it: GameResponse) = Game(
        id = it.id,
        name = it.name,
        released = it.released,
        tba = it.tba,
        metaScore = it.metaScore,
        backgroundImage = it.backgroundImage,
        rating = it.rating,
        ratingTop = it.ratingTop,
        updated = it.updated,
        isFavorite = false,
        genres = it.genres?.map { it.toDomain() } ?: listOf(),
        platforms = it.parentPlatforms?.map { it.toDomain() } ?: listOf(),
        tags = it.tags?.map { it.toDomain() } ?: listOf(),
        esrbRating = it.esrbRating?.name,
        screenshots = it.shortScreenshots?.map { it.toDomain() } ?: listOf()
    )

    private fun GameResponse.GenreResponse.toDomain() = name
    private fun GameResponse.ParentPlatformResponse.toDomain() = platform.name
    private fun GameResponse.TagResponse.toDomain() = name
    private fun GameResponse.ScreenshotResponse.toDomain() = image
}
