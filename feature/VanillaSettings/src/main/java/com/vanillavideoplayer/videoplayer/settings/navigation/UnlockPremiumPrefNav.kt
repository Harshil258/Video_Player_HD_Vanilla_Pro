package com.vanillavideoplayer.videoplayer.settings.navigation

import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.vanillavideoplayer.videoplayer.core.ui.designsystem.NavAnim
import com.vanillavideoplayer.videoplayer.settings.screens.purchase.UpgradeToPremiumScreen

const val unlockPremiumPrefNavRoute = "unlock_premium_pref_route"


fun NavGraphBuilder.unlockPremiumPrefScreen(
    onNavigateUp: () -> Unit,
    onPurchase: () -> Unit,
    formattedPrice: MutableState<String>
) {
    composable(
        route = unlockPremiumPrefNavRoute,
        enterTransition = { NavAnim.slideIn },
        popExitTransition = { NavAnim.slideOut },
        popEnterTransition = null
    ) {
        UpgradeToPremiumScreen(
            onNavigateUp = onNavigateUp,
            onPurchase = onPurchase,
            formattedPrice = formattedPrice
        )
    }
}

fun NavController.navToUnlockPremiumPref(navOptions: NavOptions? = null) {
    this.navigate(unlockPremiumPrefNavRoute, navOptions)
}
