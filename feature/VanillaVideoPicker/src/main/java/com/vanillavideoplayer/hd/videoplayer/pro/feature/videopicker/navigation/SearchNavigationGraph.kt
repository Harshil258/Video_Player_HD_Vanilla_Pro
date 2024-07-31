package com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.harshil258.adplacer.interfaces.InterAdCallBack
import com.harshil258.adplacer.utils.Constants.adPlacerApplication
import com.harshil258.adplacer.utils.Constants.runningActivity
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.designsystem.NavAnim
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.PlayerViewModel
import com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.screens.media.FilePickerViewModel
import com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.screens.search.SEARCH_ROUTE_CONST
import com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.screens.search.SearchScreenRoute

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
    onVideoClick: (uri: Uri) -> Unit, filePickerViewModel: FilePickerViewModel, playerViewModel: PlayerViewModel
) {
    composable(
        route = SEARCH_ROUTE_CONST,
        enterTransition = { NavAnim.slideIn },
        popExitTransition = { NavAnim.slideOut }) {
        SearchScreenRoute(
            onNavigateUp = onNavigateUp,
            playerViewModel = playerViewModel, viewModel = filePickerViewModel, onPlayVideo = onVideoClick
        )
    }
}
