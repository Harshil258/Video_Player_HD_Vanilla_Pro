package com.vanillavideoplayer.hd.videoplayer.pro.compose

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import com.harshil258.adplacer.adViews.BannerView
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.R
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaCancelButton
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaDoneButton
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaPlayerCenterAlignedTopBar
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaPlayerDialog
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.designsystem.VanillaIcons
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.PlayerViewModel
import com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.screens.media.FilePickerViewModel
import com.vanillavideoplayer.hd.videoplayer.pro.nav.MEDIA_NAV_ROUTE
import com.vanillavideoplayer.hd.videoplayer.pro.nav.mediaNavBuilder
import com.vanillavideoplayer.hd.videoplayer.pro.nav.startVanillaPlayerActivity
import kotlin.math.max


@Composable
fun ShowMainNetworkDialog(onDismiss: () -> Unit, onDone: (String) -> Unit) {
    var url by rememberSaveable { mutableStateOf("") }; VanillaPlayerDialog(onDismissRequest = onDismiss, title = { Text(stringResource(R.string.network_stream)) }, content = {
        Text(text = stringResource(R.string.enter_a_network_url));
        Spacer(
            modifier = Modifier.height(10.dp)
        )
        OutlinedTextField(value = url, onValueChange = { url = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text(text = stringResource(R.string.example_url)) })
    }, confirmButton = {
        VanillaDoneButton(onClick = {
            if (url.isBlank()) onDismiss() else onDone(url)
        })
    }, dismissButton = { VanillaCancelButton(onClick = onDismiss) })
}

const val MAIN_ROUTE_CONST = "main_route_const"

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(
    ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class
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
    Scaffold(floatingActionButton = {}) {

        HomeScreenBackground {
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
                                    imageVector = VanillaIcons.Settings, contentDescription = stringResource(id = R.string.settings)
                                )
                            }
                        }
                    )
                    if (permissionState.status.shouldShowRationale) RationaleDialogCompos(
                        text = stringResource(
                            id = R.string.permission_info, permissionState.permission
                        ), onConfirmButtonClick = permissionState::launchPermissionRequest
                    ) else DetailDialogCompos(
                        text = stringResource(
                            id = R.string.permission_settings, permissionState.permission
                        )
                    )
                }
            }
        }
        if (showUrlDialog) ShowMainNetworkDialog(onDismiss = { showUrlDialog = false }, onDone = { context.startVanillaPlayerActivity(Uri.parse(it)) })
    }


}


@Composable
private fun HomeScreenBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    BoxWithConstraints(
        modifier = modifier
            .background(Color(0xFFD0E4FF))
            .fillMaxSize()
    ) {
        var width = maxWidth
        var height = maxHeight
        Box(
            modifier = Modifier
                .fillMaxSize()
                .radialGradientScrim(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    center = calculateRadialGradientCenter(with(LocalDensity.current) { width.toPx() },
                        with(LocalDensity.current) { -(height / (0.40.dp)) }), radius = width - 50.dp
                )
        )
        content()
    }
}


@PreviewLightDark
@Composable
fun RadialGradientPreview() {

    BoxWithConstraints(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        var width = maxWidth
        var height = maxHeight
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .radialGradientScrim(
                    color = Color(0xFF0083E9),
                    center = calculateRadialGradientCenter(with(LocalDensity.current) { width.toPx() },
                        with(LocalDensity.current) { -(height / (0.40.dp)) }), radius = width - 50.dp
                )
        )
    }
}

@Composable
fun Modifier.radialGradientScrim(
    color: Color, center: Offset = Offset(1f, 0f), // Top center (center horizontally, top vertically)
    radius: Dp = 150.dp
):
        Modifier = this.then(Modifier.background(
    brush = Brush.radialGradient(
        colors = listOf(color, Color.Transparent), tileMode = TileMode.Decal,
        center = center, radius = with(LocalDensity.current) { radius.toPx() } // Convert Dp to pixels,
    )))

@Composable
fun calculateRadialGradientCenter(boxWidthPx: Float, boxHeightPx: Float): Offset {
    val centerX = boxWidthPx / 2f // Center horizontally
    val centerY = 0.23f * boxHeightPx // Top vertically (adjust percentage as needed)
    return Offset(centerX, centerY)
}


//@Composable
//private fun HomeScreenBackground(
//    modifier: Modifier = Modifier,
//    content: @Composable BoxScope.() -> Unit
//) {
//    Box(
//        modifier = modifier
//            .background(MaterialTheme.colorScheme.background)
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .radialGradientScrim(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
//        )
//        content()
//    }
//}


fun Modifier.radialGradientScrim(color: Color): Modifier {
    val radialGradient = object : ShaderBrush() {
        override fun createShader(size: Size): Shader {
            val largerDimension = max(size.height, size.width)
            return RadialGradientShader(
                center = size.center.copy(y = size.height / 4),
                colors = listOf(color, Color.Transparent),
                radius = largerDimension / 2,
                colorStops = listOf(0f, 0.9f)
            )
        }
    }
    return this.background(radialGradient)
}
