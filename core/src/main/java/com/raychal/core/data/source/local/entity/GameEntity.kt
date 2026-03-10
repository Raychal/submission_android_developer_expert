package com.raychal.core.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.raychal.core.domain.model.GameMovie

@Entity(tableName = "game")
data class GameEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "released")
    val released: String?,

    @ColumnInfo(name = "description_raw")
    val descriptionRaw: String? = null,

    @ColumnInfo(name = "tba")
    val tba: Boolean,

    @ColumnInfo(name = "metaScore")
    val metaScore: Int,

    @ColumnInfo(name = "backgroundImage")
    val backgroundImage: String?,

    @ColumnInfo(name = "rating")
    val rating: Double,

    @ColumnInfo(name = "ratingTop")
    val ratingTop: Int,

    @ColumnInfo(name = "updated")
    val updated: String?,

    @ColumnInfo(name = "isFavorite")
    var isFavorite: Boolean = false,

    @ColumnInfo(name = "genres")
    val genres: List<String>,

    @ColumnInfo(name = "platforms")
    val platforms: List<String>,

    @ColumnInfo(name = "tags")
    val tags: List<String>,

    @ColumnInfo(name = "esrbRating")
    val esrbRating: String?,

    @ColumnInfo(name = "screenshots")
    val screenshots: List<String>,

    @ColumnInfo(name = "movies")
    val movies: List<GameMovie>
)
