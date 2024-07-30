package com.vanillavideoplayer.videoplayer.feature.videopicker.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.harshil258.adplacer.adViews.BannerView
import com.harshil258.adplacer.interfaces.InterAdCallBack
import com.harshil258.adplacer.utils.Constants.adPlacerApplication
import com.harshil258.adplacer.utils.Constants.runningActivity
import com.vanillavideoplayer.videoplayer.core.ui.designsystem.NavAnim
import com.vanillavideoplayer.videoplayer.feature.player.PlayerViewModel
import com.vanillavideoplayer.videoplayer.feature.videopicker.screens.media.MediaPickerViewModel
import com.vanillavideoplayer.videoplayer.feature.videopicker.screens.search.MediaSearchRoute
import com.vanillavideoplayer.videoplayer.feature.videopicker.screens.search.SEARCH_ROUTE_CONST

fun NavController.navToSearchScreen(
    navOptions: NavOptions? = null
) {

    runningActivity?.let {
        adPlacerApplication.interstitialManager.loadAndShowInter(it,object : InterAdCallBack {
        override fun onContinueFlow() {
            navigate(SEARCH_ROUTE_CONST, navOptions)
        }
    })
    }
}

fun NavGraphBuilder.searchScreen(
    onNavigateUp: () -> Unit,
    onVideoClick: (uri: Uri) -> Unit,
    mediaPickerViewModel: MediaPickerViewModel,
    playerViewModel: PlayerViewModel,
    androidViewBannerCache: MutableMap<Int, BannerView>,
) {
    composable(
        route = SEARCH_ROUTE_CONST,
        enterTransition = { NavAnim.slideIn },
        popExitTransition = { NavAnim.slideOut }) {
        MediaSearchRoute(
            onNavigateUp = onNavigateUp,
            playerViewModel = playerViewModel,
            viewModel = mediaPickerViewModel,
            onPlayVideo = onVideoClick,
            androidViewBannerCache = androidViewBannerCache
        )
    }
}
