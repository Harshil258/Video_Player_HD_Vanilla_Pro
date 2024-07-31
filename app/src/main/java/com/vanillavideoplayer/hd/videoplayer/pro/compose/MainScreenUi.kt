package com.vanillavideoplayer.hd.videoplayer.pro.compose

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import com.harshil258.adplacer.adViews.BannerView
import com.vanillavideoplayer.videoplayer.core.ui.R
import com.vanillavideoplayer.videoplayer.core.ui.composviews.VanillaCancelButton
import com.vanillavideoplayer.videoplayer.core.ui.composviews.VanillaDoneButton
import com.vanillavideoplayer.videoplayer.core.ui.composviews.VanillaPlayerCenterAlignedTopBar
import com.vanillavideoplayer.videoplayer.core.ui.composviews.VanillaPlayerDialog
import com.vanillavideoplayer.videoplayer.core.ui.designsystem.VanillaIcons
import com.vanillavideoplayer.videoplayer.feature.player.PlayerViewModel
import com.vanillavideoplayer.videoplayer.feature.videopicker.screens.media.FilePickerViewModel
import com.vanillavideoplayer.hd.videoplayer.pro.nav.MEDIA_NAV_ROUTE
import com.vanillavideoplayer.hd.videoplayer.pro.nav.mediaNavBuilder
import com.vanillavideoplayer.hd.videoplayer.pro.nav.startVanillaPlayerActivity


@Composable
fun ShowMainNetworkDialog(onDismiss: () -> Unit, onDone: (String) -> Unit) {
    var url by rememberSaveable { mutableStateOf("") }; VanillaPlayerDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.network_stream)) },
        content = {
            Text(text = stringResource(R.string.enter_a_network_url));
            Spacer(
                modifier = Modifier.height(10.dp)
            )
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = stringResource(R.string.example_url)) })
        },
        confirmButton = {
            VanillaDoneButton(onClick = {
                if (url.isBlank()) onDismiss() else onDone(url)
            })
        },
        dismissButton = { VanillaCancelButton(onClick = onDismiss) })
}

const val MAIN_ROUTE_CONST = "main_route_const"

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(
    ExperimentalPermissionsApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun MainScreenUi(
    permissionState: PermissionState,
    mainNavController: NavHostController,
    mediaNavController: NavHostController,
    playerViewModel: PlayerViewModel,
    filePickerViewModel: FilePickerViewModel,
    androidViewBannerCache: MutableMap<Int, BannerView>,
    onSettingClick: () -> Unit
) {
    val context = LocalContext.current
    var showUrlDialog by rememberSaveable { mutableStateOf(false) }
    val videoFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { it?.let(context::startVanillaPlayerActivity) })
    Scaffold(floatingActionButton = {}) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .consumeWindowInsets(it)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
        ) {
            if (permissionState.status.isGranted) {
                NavHost(navController = mediaNavController, startDestination = MEDIA_NAV_ROUTE) {
                    mediaNavBuilder(
                        context = context,
                        mainNavController = mainNavController,
                        mediaNavController = mediaNavController,
                        playerViewModel = playerViewModel,
                        filePickerViewModel = filePickerViewModel,
                        androidViewBannerCache = androidViewBannerCache
                    )
                }
            } else {
                VanillaPlayerCenterAlignedTopBar(
                    s = stringResource(id = R.string.app_name),
                    icon = {
                        IconButton(onClick = onSettingClick) {
                            Icon(
                                imageVector = VanillaIcons.Settings,
                                contentDescription = stringResource(id = R.string.settings)
                            )
                        }
                    })
                if (permissionState.status.shouldShowRationale)
                    RationaleDialogCompos(
                        text = stringResource(
                            id = R.string.permission_info,
                            permissionState.permission
                        ), onConfirmButtonClick = permissionState::launchPermissionRequest
                    ) else DetailDialogCompos(
                    text = stringResource(
                        id = R.string.permission_settings,
                        permissionState.permission
                    )
                )
            }
        }
        if (showUrlDialog) ShowMainNetworkDialog(
            onDismiss = { showUrlDialog = false },
            onDone = { context.startVanillaPlayerActivity(Uri.parse(it)) })
    }

}
