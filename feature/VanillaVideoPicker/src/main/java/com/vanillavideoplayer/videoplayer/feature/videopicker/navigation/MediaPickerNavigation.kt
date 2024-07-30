package com.vanillavideoplayer.videoplayer.feature.videopicker.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.harshil258.adplacer.adViews.BannerView
import com.vanillavideoplayer.videoplayer.feature.player.PlayerViewModel
import com.vanillavideoplayer.videoplayer.feature.videopicker.screens.media.MediaPickerRoute
import com.vanillavideoplayer.videoplayer.feature.videopicker.screens.media.MediaPickerViewModel

const val mpNavigationRoute = "media_picker_screen"

fun NavController.navigateToMPScreen(navOptions: NavOptions? = null) {
    this.navigate(mpNavigationRoute, navOptions)
}

fun NavGraphBuilder.mediaPickerUi(
    onSettingsClick: () -> Unit,
    onPlayVideo: (uri: Uri) -> Unit,
    onFolderClick: (path: String) -> Unit,
    onSearchClick: () -> Unit,
    playerViewModel: PlayerViewModel,
    mediaPickerViewModel: MediaPickerViewModel,
    androidViewBannerCache: MutableMap<Int, BannerView>,
) {
    composable(route = mpNavigationRoute) {
        MediaPickerRoute(
            onSettingsClick = onSettingsClick,
            onPlayVideo = onPlayVideo,
            onFolderClick = onFolderClick,
            onSearchClick = onSearchClick,
            playerViewModel = playerViewModel,
            viewModel = mediaPickerViewModel,
            androidViewBannerCache = androidViewBannerCache,
        )
    }
}

