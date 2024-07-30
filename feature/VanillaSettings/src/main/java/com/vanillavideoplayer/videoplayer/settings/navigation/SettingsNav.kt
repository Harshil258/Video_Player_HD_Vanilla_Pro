package com.vanillavideoplayer.videoplayer.settings.navigation

import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.harshil258.adplacer.interfaces.InterAdCallBack
import com.harshil258.adplacer.utils.Constants.adPlacerApplication
import com.harshil258.adplacer.utils.Constants.runningActivity
import com.vanillavideoplayer.videoplayer.core.ui.designsystem.NavAnim
import com.vanillavideoplayer.videoplayer.settings.SettingEnum
import com.vanillavideoplayer.videoplayer.settings.SettingsScreenUi

const val settingsNavRoute = "settings_route"


fun NavGraphBuilder.settingsScreen(
    onNavigateUp: () -> Unit,
    onItemClick: (SettingEnum) -> Unit,
    isPurchased: MutableState<Boolean>
) {
    composable(
        route = settingsNavRoute,
        enterTransition = { NavAnim.slideIn },
        popExitTransition = { NavAnim.slideOut },
        popEnterTransition = null
    ) {
        SettingsScreenUi(onNavigateUp = onNavigateUp, onItemClick = onItemClick, isPurchased = isPurchased)
    }
}

fun NavController.navigateToSettings(navOptions: NavOptions? = null) {
    runningActivity?.let {
        adPlacerApplication.interstitialManager.loadAndShowInter(it,object : InterAdCallBack {
        override fun onContinueFlow() {
            navigate(settingsNavRoute, navOptions)
        }
    })
    }
}
