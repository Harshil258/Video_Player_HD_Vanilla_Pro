package com.vanillavideoplayer.videoplayer.settings.screens.medialibrary

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.harshil258.adplacer.utils.STATUS
import com.harshil258.adplacer.utils.SharedPrefConfig.Companion.sharedPrefConfig
import com.vanillavideoplayer.videoplayer.core.ui.NativeAdCompose
import com.vanillavideoplayer.videoplayer.core.ui.R
import com.vanillavideoplayer.videoplayer.core.ui.composviews.VanillaChoosablePrefView
import com.vanillavideoplayer.videoplayer.core.ui.composviews.VanillaPlayerTopBar
import com.vanillavideoplayer.videoplayer.core.ui.designsystem.VanillaIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderPreferencesScreen(
    onNavigateUp: () -> Unit,
    viewModel: MediaLibraryPreferencesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.RESUMED)
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val scrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {
            VanillaPlayerTopBar(
                s = stringResource(id = R.string.manage_folders),
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
            modifier = Modifier.fillMaxSize()
        ) {
            if(sharedPrefConfig.appDetails.adStatus == STATUS.ON.name){
                NativeAdCompose()
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (uiState) {
                    FolderPreferencesUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                    is FolderPreferencesUiState.Success -> LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items((uiState as FolderPreferencesUiState.Success).directories) { folder ->
                            VanillaChoosablePrefView(
                                title = folder.name,
                                description = folder.path,
                                selected = folder.path in preferences.excludeFolders,
                                onClick = { viewModel.updateExcludeList(folder.path) }
                            )
                        }
                    }
                }
            }
        }
    }
}
