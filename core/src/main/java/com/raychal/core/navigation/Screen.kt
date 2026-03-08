package com.raychal.core.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Home : Screen

    @Serializable
    data object Favorite : Screen

    @Serializable
    data class Detail(val gameId: Int) : Screen
}
