package com.vanillavideoplayer.videoplayer.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.vanillavideoplayer.videoplayer.core.ui.designsystem.NavAnim
import com.vanillavideoplayer.videoplayer.settings.screens.subtitle.SubtitlePrefScreen

const val subtitlePrefNavRoute = "sub_header_pref_route"

fun NavGraphBuilder.subtitlePrefScreen(onNavigateUp: () -> Unit) {
    composable(
        route = subtitlePrefNavRoute,
        enterTransition = { NavAnim.slideIn },
        popExitTransition = { NavAnim.slideOut },
        popEnterTransition = null
    ) {
        SubtitlePrefScreen(onNavUp = onNavigateUp)
    }
}


fun NavController.navToSubtitlePref(navOptions: NavOptions? = null) {
    this.navigate(subtitlePrefNavRoute, navOptions)
}
