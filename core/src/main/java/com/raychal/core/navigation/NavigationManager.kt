package com.raychal.core.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class NavigationManager {
    private val _navigationCommands = Channel<NavigationCommand>(Channel.BUFFERED)
    val navigationCommands = _navigationCommands.receiveAsFlow()

    fun navigate(route: Any) {
        _navigationCommands.trySend(NavigationCommand.Navigate(route))
    }

    fun navigateUp() {
        _navigationCommands.trySend(NavigationCommand.NavigateUp)
    }
}
