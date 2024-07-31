package com.vanillavideoplayer.hd.videoplayer.pro.settings.screens.decoder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.DecoderPriority
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.R
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.RadioStringBtnView
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaClickablePrefItem
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaPlayerTopBar
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.designsystem.VanillaIcons
import com.vanillavideoplayer.hd.videoplayer.pro.settings.composables.SubtitlePrefs
import com.vanillavideoplayer.hd.videoplayer.pro.settings.composables.chooserDialog
import com.vanillavideoplayer.hd.videoplayer.pro.settings.x.nameString

@Composable
fun DecoderPrioritySetting(
    currentValue: DecoderPriority,
    onClick: () -> Unit
) {
    VanillaClickablePrefItem(
        title = stringResource(R.string.decoder_priority),
        description = currentValue.nameString(),
        icon = VanillaIcons.Priority,
        onClick = onClick
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecoderPreferencesScreen(
    onNavigateUp: () -> Unit,
    viewModel: DecoderPreferencesViewModel = hiltViewModel()
) {
    val preferences by viewModel.preferencesFlow.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val scrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {
            VanillaPlayerTopBar(
                s = stringResource(id = R.string.decoder),
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
            SubtitlePrefs(string = stringResource(id = R.string.playback))
            DecoderPrioritySetting(
                currentValue = preferences.decoderPriority,
                onClick = { viewModel.showDialog(DecoderPreferenceDialog.DecoderPriorityDialog) }
            )
        }

        uiState.showDialog?.let { showDialog ->
            when (showDialog) {
                DecoderPreferenceDialog.DecoderPriorityDialog -> {
                    chooserDialog(
                        header = stringResource(id = R.string.decoder_priority),
                        onDismissDialog = viewModel::hideDialog
                    ) {
                        items(DecoderPriority.entries.toTypedArray()) {
                            RadioStringBtnView(
                                s = it.nameString(),
                                selected = it == preferences.decoderPriority,
                                function = {
                                    viewModel.updateDecoderPriority(it)
                                    viewModel.hideDialog()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

