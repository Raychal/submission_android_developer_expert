package com.raychal.core.navigation

import androidx.navigation.NavOptionsBuilder

sealed class NavigationCommand {
    data class Navigate(
        val route: Any,
        val navOptions: NavOptionsBuilder.() -> Unit = {}
    ) : NavigationCommand()

    data object NavigateUp : NavigationCommand()
}
