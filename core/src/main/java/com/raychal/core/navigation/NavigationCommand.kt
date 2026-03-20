package com.raychal.core.navigation

sealed class NavigationCommand {
    data class Navigate(
        val route: Any
    ) : NavigationCommand()

    data object NavigateUp : NavigationCommand()
}
