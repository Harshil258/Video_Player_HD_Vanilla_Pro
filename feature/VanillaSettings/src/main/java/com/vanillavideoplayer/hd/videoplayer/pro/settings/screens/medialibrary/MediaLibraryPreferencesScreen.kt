package com.vanillavideoplayer.hd.videoplayer.pro.settings.screens.medialibrary

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.vanillavideoplayer.hd.videoplayer.pro.core.common.extra.scanStoragePath
import com.vanillavideoplayer.hd.videoplayer.pro.core.common.extra.visibleToast
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.NativeAdCompose
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.R
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaClickablePrefItem
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaPlayerTopBar
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.designsystem.VanillaIcons
import com.vanillavideoplayer.hd.videoplayer.pro.settings.composables.SubtitlePrefs
import kotlinx.coroutines.launch

@Composable
fun ForceRescanStorageSetting(
    onClick: () -> Unit
) {
    VanillaClickablePrefItem(
        title = stringResource(id = R.string.force_rescan_storage),
        description = stringResource(id = R.string.force_rescan_storage_desc),
        icon = VanillaIcons.Update,
        onClick = onClick
    )
}

@Composable
fun HideFoldersSettings(
    onClick: () -> Unit
) {
    VanillaClickablePrefItem(
        title = stringResource(id = R.string.manage_folders),
        description = stringResource(id = R.string.manage_folders_desc),
        icon = VanillaIcons.FolderOff,
        onClick = onClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaLibraryPreferencesScreen(
    onNavigateUp: () -> Unit,
    onFolderSettingClick: () -> Unit = {}
) {
    val scrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {
            VanillaPlayerTopBar(
                s = stringResource(id = R.string.media_library),
                behavior = scrollBehaviour,
                icon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = VanillaIcons.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_up)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(state = rememberScrollState())
        ) {
            SubtitlePrefs(string = stringResource(id = R.string.scan))
            ForceRescanStorageSetting(
                onClick = {
                    scope.launch { context.scanStoragePath() }
                    context.visibleToast(
                        string = context.getString(R.string.scanning_storage),
                        duration = Toast.LENGTH_LONG
                    )
                }
            )
            NativeAdCompose()
            HideFoldersSettings(
                onClick = onFolderSettingClick
            )

        }
    }
}


