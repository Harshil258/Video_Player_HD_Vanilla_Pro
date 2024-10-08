package com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.navigation

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.harshil258.adplacer.interfaces.InterAdCallBack
import com.harshil258.adplacer.utils.Constants.adPlacerApplication
import com.harshil258.adplacer.utils.Constants.runningActivity
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.designsystem.NavAnim
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.PlayerViewModel
import com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.screens.mediaFolder.FolderScreenRoute

fun NavController.navigationToFolderScreen(
    folderId: String, navOptions: NavOptions? = null
) {
    runningActivity?.let {
        adPlacerApplication.interstitialManager.loadAndShowInter(it,object : InterAdCallBack {
        override fun onContinueFlow() {
            val encodedFolderId = Uri.encode(folderId)
            navigate("$mpDirNavRoute/$encodedFolderId", navOptions)
        }
    })
    }
}

internal class DirArgs(val folderId: String) {
    constructor(savedStateHandle: SavedStateHandle) : this(Uri.decode(checkNotNull(savedStateHandle[dirIdArg])))
}

fun NavGraphBuilder.folderScreen(
    onNavigateUp: () -> Unit,
    onVideoClick: (uri: Uri) -> Unit,
    playerViewModel: PlayerViewModel,
) {
    composable(
        route = "$mpDirNavRoute/{$dirIdArg}",
        arguments = listOf(navArgument(dirIdArg) { type = NavType.StringType }),
        enterTransition = { NavAnim.slideIn },
        popExitTransition = { NavAnim.slideOut }) {
        FolderScreenRoute(
            onVideoClick = onVideoClick,
            onNavigateUp = onNavigateUp,
            playerViewModel = playerViewModel,
        )
    }
}


const val mpDirNavRoute = "media_picker_folder_screen"
internal const val dirIdArg = "folderId"