package com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.harshil258.adplacer.interfaces.InterAdCallBack
import com.harshil258.adplacer.utils.Constants.adPlacerApplication
import com.harshil258.adplacer.utils.Constants.runningActivity
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.designsystem.NavAnim
import com.vanillavideoplayer.hd.videoplayer.pro.settings.screens.language.LangPrefScreen

const val langPrefNavRoute = "lang_pref_route"


fun NavGraphBuilder.langPrefScreen(shouldShowBackNavigation: Boolean = true, onNavigateUp: () -> Unit) {
    composable(route = langPrefNavRoute, enterTransition = { NavAnim.slideIn }, popExitTransition = { NavAnim.slideOut }, popEnterTransition = null
    ) {
        LangPrefScreen(
            onNavigateUp = onNavigateUp, shouldShowBackNavigation = shouldShowBackNavigation
        )
    }
}

fun NavController.navToLangPref(navOptions: NavOptions? = null) {

    runningActivity?.let {
        adPlacerApplication.interstitialManager.loadAndShowInter(it, object : InterAdCallBack {
            override fun onContinueFlow() {
                navigate(langPrefNavRoute, navOptions)
            }
        })
    }
}
