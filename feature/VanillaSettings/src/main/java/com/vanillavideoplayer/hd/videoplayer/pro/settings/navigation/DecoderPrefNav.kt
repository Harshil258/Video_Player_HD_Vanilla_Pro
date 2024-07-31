package com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.designsystem.NavAnim
import com.vanillavideoplayer.hd.videoplayer.pro.settings.screens.decoder.DecoderPreferencesScreen


fun NavController.navToDecoderPref(navOptions: NavOptions? = null) {
    this.navigate(decoderPrefNavRoute, navOptions)
}

const val decoderPrefNavRoute = "decoder_pref_nav_route"


fun NavGraphBuilder.decoderPrefScreen(onNavigateUp: () -> Unit) {
    composable(
        route = decoderPrefNavRoute,
        enterTransition = { NavAnim.slideIn },
        popExitTransition = { NavAnim.slideOut },
        popEnterTransition = null
    ) {
        DecoderPreferencesScreen(onNavigateUp = onNavigateUp)
    }
}
