package com.vanillavideoplayer.videoplayer.feature.videopicker.navigation

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.vanillavideoplayer.videoplayer.settings.SettingEnum
import com.vanillavideoplayer.videoplayer.settings.navigation.aboutPrefScreen
import com.vanillavideoplayer.videoplayer.settings.navigation.appearancePrefScreen
import com.vanillavideoplayer.videoplayer.settings.navigation.audioPrefScreen
import com.vanillavideoplayer.videoplayer.settings.navigation.decoderPrefScreen
import com.vanillavideoplayer.videoplayer.settings.navigation.dirPrefScreen
import com.vanillavideoplayer.videoplayer.settings.navigation.langPrefScreen
import com.vanillavideoplayer.videoplayer.settings.navigation.mediaLibPrefScreen
import com.vanillavideoplayer.videoplayer.settings.navigation.navToAboutPref
import com.vanillavideoplayer.videoplayer.settings.navigation.navToAppearancePref
import com.vanillavideoplayer.videoplayer.settings.navigation.navToAudioPref
import com.vanillavideoplayer.videoplayer.settings.navigation.navToDecoderPref
import com.vanillavideoplayer.videoplayer.settings.navigation.navToDirPrefScreen
import com.vanillavideoplayer.videoplayer.settings.navigation.navToLangPref
import com.vanillavideoplayer.videoplayer.settings.navigation.navToMediaLibPrefScreen
import com.vanillavideoplayer.videoplayer.settings.navigation.navToPlayerPref
import com.vanillavideoplayer.videoplayer.settings.navigation.navToSubtitlePref
import com.vanillavideoplayer.videoplayer.settings.navigation.navToUnlockPremiumPref
import com.vanillavideoplayer.videoplayer.settings.navigation.playerPrefScreen
import com.vanillavideoplayer.videoplayer.settings.navigation.settingsNavRoute
import com.vanillavideoplayer.videoplayer.settings.navigation.settingsScreen
import com.vanillavideoplayer.videoplayer.settings.navigation.subtitlePrefScreen
import com.vanillavideoplayer.videoplayer.settings.navigation.unlockPremiumPrefScreen

const val SETTINGS_ROUTE_VAL = "settings_nav_route"

fun NavGraphBuilder.settingsNavGraph(
    navController: NavHostController,
    onPurchase: () -> Unit,
    formattedPrice: MutableState<String>,
    isPurchased: MutableState<Boolean>
) {
    navigation(
        startDestination = settingsNavRoute,
        route = SETTINGS_ROUTE_VAL
    ) {
        settingsScreen(
            onNavigateUp = navController::navigateUp,
            onItemClick = { setting ->
                when (setting) {
                    SettingEnum.UNLOCK_PREMIUM -> navController.navToUnlockPremiumPref()
                    SettingEnum.APPEARANCE -> navController.navToAppearancePref()
                    SettingEnum.MEDIA_LIB -> navController.navToMediaLibPrefScreen()
                    SettingEnum.PLAYER -> navController.navToPlayerPref()
                    SettingEnum.DECODER_SETTING -> navController.navToDecoderPref()
                    SettingEnum.AUDIO_PREFS -> navController.navToAudioPref()
                    SettingEnum.SUBTITLE_DETAILS -> navController.navToSubtitlePref()
                    SettingEnum.ABOUT_US -> navController.navToAboutPref()
                    SettingEnum.LANGUAGE -> navController.navToLangPref()
                }
            }, isPurchased = isPurchased
        )
//        if (!isPurchased.value) {
            unlockPremiumPrefScreen(
                onNavigateUp = navController::navigateUp,
                onPurchase = onPurchase,
                formattedPrice = formattedPrice
            )
//        }
        appearancePrefScreen(
            onNavigateUp = navController::navigateUp
        )
        mediaLibPrefScreen(
            onNavigateUp = navController::navigateUp,
            onDirSettingSelect = navController::navToDirPrefScreen
        )
        dirPrefScreen(
            onNavigateUp = navController::navigateUp
        )
        playerPrefScreen(
            onNavigateUp = navController::navigateUp
        )
        decoderPrefScreen(
            onNavigateUp = navController::navigateUp
        )
        audioPrefScreen(
            onNavigateUp = navController::navigateUp
        )
        subtitlePrefScreen(
            onNavigateUp = navController::navigateUp
        )
        aboutPrefScreen(
            onNavigateUp = navController::navigateUp
        )
        langPrefScreen(
            onNavigateUp = navController::navigateUp
        )
    }
}
