package com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.navigation

import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.vanillavideoplayer.hd.videoplayer.pro.settings.SettingEnum
import com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation.aboutPrefScreen
import com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation.appearancePrefScreen
import com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation.audioPrefScreen
import com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation.decoderPrefScreen
import com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation.dirPrefScreen
import com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation.langPrefScreen
import com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation.mediaLibPrefScreen
import com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation.navToAboutPref
import com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation.navToAppearancePref
import com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation.navToAudioPref
import com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation.navToDecoderPref
import com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation.navToDirPrefScreen
import com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation.navToLangPref
import com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation.navToMediaLibPrefScreen
import com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation.navToPlayerPref
import com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation.navToSubtitlePref
import com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation.navToUnlockPremiumPref
import com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation.playerPrefScreen
import com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation.settingsNavRoute
import com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation.settingsScreen
import com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation.subtitlePrefScreen
import com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation.unlockPremiumPrefScreen

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
