package com.vanillavideoplayer.videoplayer.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.vanillavideoplayer.videoplayer.core.ui.designsystem.NavAnim
import com.vanillavideoplayer.videoplayer.settings.screens.player.PlayerPreferencesScreen

const val playerPrefNavRoute = "player_pref_nav_route"


fun NavGraphBuilder.playerPrefScreen(onNavigateUp: () -> Unit) {
    composable(
        route = playerPrefNavRoute,
        enterTransition = { NavAnim.slideIn },
        popExitTransition = { NavAnim.slideOut },
        popEnterTransition = null
    ) {
        PlayerPreferencesScreen(
            onNavigateUp = onNavigateUp
        )
    }
}

fun NavController.navToPlayerPref(navOptions: NavOptions? = null) {
    this.navigate(playerPrefNavRoute, navOptions)
}
