package com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.designsystem.NavAnim
import com.vanillavideoplayer.hd.videoplayer.pro.settings.screens.audio.AudioPreferencesScreen


fun NavController.navToAudioPref(navOptions: NavOptions? = null) {
    this.navigate(audioPrefNavRoute, navOptions)
}

const val audioPrefNavRoute = "audio_pref_nav_route"

fun NavGraphBuilder.audioPrefScreen(onNavigateUp: () -> Unit) {
    composable(
        route = audioPrefNavRoute,
        enterTransition = { NavAnim.slideIn },
        popExitTransition = { NavAnim.slideOut },
        popEnterTransition = null
    ) {
        AudioPreferencesScreen(onNavigateUp = onNavigateUp)
    }
}
