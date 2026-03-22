package com.raychal.core.utils

import com.raychal.core.data.source.local.entity.GameEntity
import com.raychal.core.data.source.remote.response.GameResponse
import com.raychal.core.data.source.remote.response.ListGenreResponse
import com.raychal.core.data.source.remote.response.MoviesGameResponse
import com.raychal.core.data.source.remote.response.ScreenshotsGameResponse
import com.raychal.core.domain.model.Game
import com.raychal.core.domain.model.GameMovie
import com.raychal.core.domain.model.Genre

object DataMapper {
    fun mapEntitiesToDomain(input: List<GameEntity>): List<Game> =
        input.map {
            Game(
                id = it.id,
                name = it.name,
                released = it.released,
                descriptionRaw = it.descriptionRaw,
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
                screenshots = it.screenshots,
                movies = it.movies
            )
        }

    fun mapDomainToEntity(input: Game) = GameEntity(
        id = input.id,
        name = input.name,
        released = input.released,
        descriptionRaw = input.descriptionRaw,
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
        screenshots = input.screenshots,
        movies = input.movies
    )

    fun mapResponseToDomain(
        gameResponse: GameResponse,
        screenshotsResponse: ScreenshotsGameResponse? = null,
        moviesResponse: MoviesGameResponse? = null
    ) = Game(
        id = gameResponse.id,
        name = gameResponse.name,
        released = gameResponse.released,
        descriptionRaw = gameResponse.descriptionRaw,
        tba = gameResponse.tba,
        metaScore = gameResponse.metaScore,
        backgroundImage = gameResponse.backgroundImage,
        rating = gameResponse.rating,
        ratingTop = gameResponse.ratingTop,
        updated = gameResponse.updated,
        isFavorite = false,
        genres = gameResponse.genres?.map { it.name } ?: listOf(),
        platforms = gameResponse.parentPlatforms?.map { it.platform.name } ?: listOf(),
        tags = gameResponse.tags?.map { it.name } ?: listOf(),
        esrbRating = gameResponse.esrbRating?.name,
        screenshots = screenshotsResponse?.results?.map { it.image } ?: listOf(),
        movies = moviesResponse?.results?.map {
            GameMovie(
                name = it.name,
                preview = it.preview,
                url = it.data.max
            )
        } ?: listOf()
    )

    fun mapGenreResponseToDomain(input: List<ListGenreResponse.GenreResponse>): List<Genre> {
        return input.map {
            Genre(id = it.id, name = it.name)
        }
    }
}
