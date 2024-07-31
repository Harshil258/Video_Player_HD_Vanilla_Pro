package com.vanillavideoplayer.hd.videoplayer.pro.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.harshil258.adplacer.interfaces.InterAdCallBack
import com.harshil258.adplacer.utils.Constants.adPlacerApplication
import com.harshil258.adplacer.utils.Constants.runningActivity
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.designsystem.NavAnim
import com.vanillavideoplayer.hd.videoplayer.pro.settings.screens.onboarding.OnboardingScreen

const val onboardingPrefNavRoute = "onboarding_pref_route"


fun NavGraphBuilder.onboardingPrefScreen(onGetStarted: () -> Unit) {
    composable(route = onboardingPrefNavRoute, enterTransition = { NavAnim.slideIn }, popExitTransition = { NavAnim.slideOut }, popEnterTransition = null
    ) {
        OnboardingScreen(
            onGetStarted = onGetStarted
        )
    }
}

fun NavController.navToOnboardingPref(navOptions: NavOptions? = null) {
    runningActivity?.let {
        adPlacerApplication.interstitialManager.loadAndShowInter(it, object : InterAdCallBack {
            override fun onContinueFlow() {
                navigate(onboardingPrefNavRoute, navOptions)
            }
        })
    }
}
