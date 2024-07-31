package com.vanillavideoplayer.hd.videoplayer.pro.nav

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.harshil258.adplacer.adViews.BannerView
import com.harshil258.adplacer.interfaces.InterAdCallBack
import com.harshil258.adplacer.utils.Constants.adPlacerApplication
import com.harshil258.adplacer.utils.Constants.runningActivity
import com.vanillavideoplayer.videoplayer.feature.player.PlayerViewModel
import com.vanillavideoplayer.videoplayer.feature.player.VanillaPlayerActivityPro
import com.vanillavideoplayer.videoplayer.feature.videopicker.navigation.mediaPickerUi
import com.vanillavideoplayer.videoplayer.feature.videopicker.navigation.folderScreen
import com.vanillavideoplayer.videoplayer.feature.videopicker.navigation.mpNavigationRoute
import com.vanillavideoplayer.videoplayer.feature.videopicker.navigation.navigationToFolderScreen
import com.vanillavideoplayer.videoplayer.feature.videopicker.navigation.navToSearchScreen
import com.vanillavideoplayer.videoplayer.feature.videopicker.navigation.searchScreen
import com.vanillavideoplayer.videoplayer.feature.videopicker.screens.media.FilePickerViewModel
import com.vanillavideoplayer.videoplayer.settings.navigation.navigateToSettings


fun NavGraphBuilder.mediaNavBuilder(
    context: Context,
    mainNavController: NavHostController,
    mediaNavController: NavHostController,
    playerViewModel: PlayerViewModel,
    filePickerViewModel: FilePickerViewModel,
    androidViewBannerCache: MutableMap<Int, BannerView>
) {
    navigation(startDestination = mpNavigationRoute, route = MEDIA_NAV_ROUTE) {
        mediaPickerUi(
            onPlayVideo = context::startVanillaPlayerActivity,
            onFolderClick = mediaNavController::navigationToFolderScreen,
            onSearchClick = mediaNavController::navToSearchScreen,
            onSettingsClick = mainNavController::navigateToSettings,
            playerViewModel = playerViewModel,
            filePickerViewModel = filePickerViewModel,androidViewBannerCache = androidViewBannerCache,
        )
        folderScreen(
            onNavigateUp = mediaNavController::navigateUp,
            onVideoClick = context::startVanillaPlayerActivity,
            playerViewModel = playerViewModel
        )
        searchScreen(
            onNavigateUp = mediaNavController::navigateUp,
            onVideoClick = context::startVanillaPlayerActivity,
            filePickerViewModel = filePickerViewModel,
            playerViewModel = playerViewModel
        )
    }
}

const val MEDIA_NAV_ROUTE = "media_nav_route"

fun Context.startVanillaPlayerActivity(uri: Uri) {

    runningActivity?.let {
        adPlacerApplication.interstitialManager.loadAndShowInter(it,object : InterAdCallBack {
        override fun onContinueFlow() {
            val intent = Intent(
                Intent.ACTION_VIEW,
                uri,
                this@startVanillaPlayerActivity,
                VanillaPlayerActivityPro::class.java
            )
            startActivity(intent)
        }
    })
    }
}

