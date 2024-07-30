package com.vanillavideoplayer.videoplayer.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.vanillavideoplayer.videoplayer.core.ui.designsystem.NavAnim
import com.vanillavideoplayer.videoplayer.settings.screens.about.AboutPreferencesScreen

const val aboutPrefNavRoute = "about_pref_route"


fun NavGraphBuilder.aboutPrefScreen(onNavigateUp: () -> Unit) {
    composable(
        route = aboutPrefNavRoute,
        enterTransition = { NavAnim.slideIn },
        popExitTransition = { NavAnim.slideOut },
        popEnterTransition = null
    ) {
        AboutPreferencesScreen(
            onNavigateUp = onNavigateUp
        )
    }
}

fun NavController.navToAboutPref(navOptions: NavOptions? = null) {
    this.navigate(aboutPrefNavRoute, navOptions)
}
