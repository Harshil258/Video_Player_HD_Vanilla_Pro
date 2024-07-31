package com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.designsystem.NavAnim
import com.vanillavideoplayer.hd.videoplayer.pro.settings.screens.appearance.AppearancePreferencesScreen


fun NavController.navToAppearancePref(navOptions: NavOptions? = null) {
    this.navigate(appearancePrefNavRoute, navOptions)
}

const val appearancePrefNavRoute = "appearance_pref_nav_route"

fun NavGraphBuilder.appearancePrefScreen(onNavigateUp: () -> Unit) {
    composable(
        route = appearancePrefNavRoute,
        enterTransition = { NavAnim.slideIn },
        popExitTransition = { NavAnim.slideOut },
        popEnterTransition = null
    ) {
        AppearancePreferencesScreen(onNavigateUp = onNavigateUp)
    }
}
