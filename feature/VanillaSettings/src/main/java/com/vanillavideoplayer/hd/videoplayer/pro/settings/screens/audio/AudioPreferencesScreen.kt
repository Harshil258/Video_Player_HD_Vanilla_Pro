package com.vanillavideoplayer.hd.videoplayer.pro.settings.screens.audio

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.NativeAdCompose
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.R
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.RadioStringBtnView
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaClickablePrefItem
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaPlayerTopBar
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaPrefSwitchView
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.designsystem.VanillaIcons
import com.vanillavideoplayer.hd.videoplayer.pro.settings.composables.SubtitlePrefs
import com.vanillavideoplayer.hd.videoplayer.pro.settings.composables.chooserDialog
import com.vanillavideoplayer.hd.videoplayer.pro.settings.utils.DesiHelperObj

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPreferencesScreen(
    onNavigateUp: () -> Unit,
    viewModel: AudioPreferencesViewModel = hiltViewModel()
) {
    val preferences by viewModel.preferencesFlow.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val languages = remember { listOf(Pair("None", "")) + DesiHelperObj.getAvailableDesis() }

    val scrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {
            VanillaPlayerTopBar(
                s = stringResource(id = R.string.audio),
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
            RequireAudioFocusSetting(
                isChecked = preferences.audioFocusNiJarurChheKeNai,
                onClick = viewModel::toggleRequireAudioFocus
            )
            PreferredAudioLanguageSetting(
                currentLanguage = DesiHelperObj.getDesiDisplayLang(preferences.suggestedAudioLang),
                onClick = { viewModel.showDialog(AudioPreferenceDialog.AudioLanguageDialog) }
            )
            ShowSystemVolumePanelSetting(
                isChecked = preferences.systemVolumePanelDekhadu,
                onClick = viewModel::toggleShowSystemVolumePanel
            )

            NativeAdCompose()
            PauseOnHeadsetDisconnectSetting(
                isChecked = preferences.boolPauseOnHeadsetDisconnect,
                onClick = viewModel::togglePauseOnHeadsetDisconnect
            )

        }

        uiState.showDialog?.let { showDialog ->
            when (showDialog) {
                AudioPreferenceDialog.AudioLanguageDialog -> {
                    chooserDialog(
                        header = stringResource(id = R.string.preferred_audio_lang),
                        onDismissDialog = viewModel::hideDialog
                    ) {
                        items(languages) {
                            RadioStringBtnView(
                                s = it.first,
                                selected = it.second == preferences.suggestedAudioLang,
                                function = {
                                    viewModel.updateAudioLanguage(it.second)
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

@Composable
fun PreferredAudioLanguageSetting(
    currentLanguage: String,
    onClick: () -> Unit
) {
    VanillaClickablePrefItem(
        title = stringResource(id = R.string.preferred_audio_lang),
        description = currentLanguage.takeIf { it.isNotBlank() } ?: stringResource(
            id = R.string.preferred_audio_lang_description
        ),
        icon = VanillaIcons.Language,
        onClick = onClick
    )
}

@Composable
fun RequireAudioFocusSetting(
    isChecked: Boolean,
    onClick: () -> Unit
) {
    VanillaPrefSwitchView(
        title = stringResource(R.string.require_audio_focus),
        description = stringResource(R.string.require_audio_focus_desc),
        icon = VanillaIcons.Focus,
        isChecked = isChecked,
        onClick = onClick
    )
}

@Composable
fun PauseOnHeadsetDisconnectSetting(
    isChecked: Boolean,
    onClick: () -> Unit
) {
    VanillaPrefSwitchView(
        title = stringResource(id = R.string.pause_on_headset_disconnect),
        description = stringResource(id = R.string.pause_on_headset_disconnect_desc),
        icon = VanillaIcons.HeadsetOff,
        isChecked = isChecked,
        onClick = onClick
    )
}

@Composable
fun ShowSystemVolumePanelSetting(
    isChecked: Boolean,
    onClick: () -> Unit
) {
    VanillaPrefSwitchView(
        title = stringResource(id = R.string.system_volume_panel),
        description = stringResource(id = R.string.system_volume_panel_desc),
        icon = VanillaIcons.Headset,
        isChecked = isChecked,
        onClick = onClick
    )
}
