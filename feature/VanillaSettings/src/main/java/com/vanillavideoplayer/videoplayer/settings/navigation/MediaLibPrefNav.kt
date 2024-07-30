package com.vanillavideoplayer.videoplayer.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.vanillavideoplayer.videoplayer.core.ui.designsystem.NavAnim
import com.vanillavideoplayer.videoplayer.settings.screens.medialibrary.FolderPreferencesScreen
import com.vanillavideoplayer.videoplayer.settings.screens.medialibrary.MediaLibraryPreferencesScreen

const val mediaLibPrefNavRoute = "media_lib_pref_route"
const val dirPrefNavRoute = "dir_pref_route"

fun NavController.navToMediaLibPrefScreen(navOptions: NavOptions? = null) {
    this.navigate(mediaLibPrefNavRoute, navOptions)
}


fun NavGraphBuilder.mediaLibPrefScreen(
    onNavigateUp: () -> Unit,
    onDirSettingSelect: () -> Unit
) {
    composable(
        route = mediaLibPrefNavRoute,
        enterTransition = { NavAnim.slideIn },
        popExitTransition = { NavAnim.slideOut },
        popEnterTransition = null
    ) {
        MediaLibraryPreferencesScreen(
            onNavigateUp = onNavigateUp,
            onFolderSettingClick = onDirSettingSelect
        )
    }
}

fun NavController.navToDirPrefScreen(navOptions: NavOptions? = null) {
    this.navigate(dirPrefNavRoute, navOptions)
}


fun NavGraphBuilder.dirPrefScreen(onNavigateUp: () -> Unit) {
    composable(
        route = dirPrefNavRoute,
        enterTransition = { NavAnim.slideIn },
        popExitTransition = { NavAnim.slideOut },
        popEnterTransition = null
    ) {
        FolderPreferencesScreen(onNavigateUp = onNavigateUp)
    }
}
