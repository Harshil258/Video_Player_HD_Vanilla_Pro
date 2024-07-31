package com.vanillavideoplayer.hd.videoplayer.pro.settings.screens.player

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vanillavideoplayer.hd.videoplayer.pro.core.common.extra.roundTheFloat
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.DoubleTapGestureEnum
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.FastSeek
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.ResumeEnum
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.ScreenOrientationEnum
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.NativeAdCompose
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.R
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.RadioStringBtnView
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaClickablePrefItem
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaPlayerDialogWithPositiveAndNagative
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaPlayerTopBar
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaPrefSwitchView
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.composviews.VanillaPrefSwitchWithDividerView
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.designsystem.VanillaIcons
import com.vanillavideoplayer.hd.videoplayer.pro.settings.composables.SubtitlePrefs
import com.vanillavideoplayer.hd.videoplayer.pro.settings.composables.chooserDialog
import com.vanillavideoplayer.hd.videoplayer.pro.settings.x.nameString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerPreferencesScreen(
    onNavigateUp: () -> Unit,
    viewModel: PlayerPreferencesViewModel = hiltViewModel()
) {
    val preferences by viewModel.preferencesFlow.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val scrollBehaviour = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {
            VanillaPlayerTopBar(
                s = stringResource(id = R.string.player_name),
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
            SubtitlePrefs(string = stringResource(id = R.string.interface_name))
            DoubleTapGestureSetting(
                isChecked = (preferences.doubleTapGestureEnum != DoubleTapGestureEnum.DTG_NONE),
                onChecked = viewModel::toggleDoubleTapGesture,
                onClick = { viewModel.showDialog(PlayerPreferenceDialog.DoubleTapDialog) }
            )
            SeekGestureSetting(
                isChecked = preferences.boolSeekControls,
                onClick = viewModel::toggleUseSeekControls
            )
            SwipeGestureSetting(
                isChecked = preferences.boolSwipeControls,
                onClick = viewModel::toggleUseSwipeControls
            )
            ZoomGestureSetting(
                isChecked = preferences.boolZoomControls,
                onClick = viewModel::toggleUseZoomControls
            )
            LongPressGesture(
                isChecked = preferences.boolLongPressControls,
                onChecked = viewModel::toggleUseLongPressControls,
                playbackSpeed = preferences.longPressControlsSpeedVal,
                onClick = { viewModel.showDialog(PlayerPreferenceDialog.LongPressControlsSpeedDialog) }
            )
            NativeAdCompose()
            ControllerTimeoutPreference(
                currentValue = preferences.optionsAutoHideTimeout,
                onClick = { viewModel.showDialog(PlayerPreferenceDialog.ControllerTimeoutDialog) }
            )

            SeekIncrementPreference(
                currentValue = preferences.seekIncrementVal,
                onClick = { viewModel.showDialog(PlayerPreferenceDialog.SeekIncrementDialog) }
            )
            SubtitlePrefs(string = stringResource(id = R.string.playback))
            DefaultPlaybackSpeedSetting(
                currentDefaultPlaybackSpeed = preferences.defaultPlaySpeed,
                onClick = { viewModel.showDialog(PlayerPreferenceDialog.PlaybackSpeedDialog) }
            )

            AutoplaySetting(
                isChecked = preferences.boolAutoplay,
                onClick = viewModel::toggleAutoplay
            )

            RememberBrightnessSetting(
                isChecked = preferences.savePlayerBright,
                onClick = viewModel::toggleRememberBrightnessLevel
            )
            RememberSelectionsSetting(
                isChecked = preferences.saveSelections,
                onClick = viewModel::toggleRememberSelections
            )
            FastSeekSetting(
                isChecked = (preferences.fastSeek != FastSeek.DISABLE),
                onChecked = viewModel::toggleFastSeek,
                onClick = { viewModel.showDialog(PlayerPreferenceDialog.FastSeekDialog) }
            )

            ResumeSetting(
                onClick = { viewModel.showDialog(PlayerPreferenceDialog.ResumeDialog) }
            )
            ScreenOrientationSetting(
                currentOrientationPreference = preferences.playerScreenOrientationEnum,
                onClick = {
                    viewModel.showDialog(PlayerPreferenceDialog.PlayerScreenOrientationDialog)
                }
            )

        }

        uiState.showDialog?.let { showDialog ->
            when (showDialog) {
                PlayerPreferenceDialog.ResumeDialog -> {
                    chooserDialog(
                        header = stringResource(id = R.string.resume),
                        onDismissDialog = viewModel::hideDialog
                    ) {
                        items(ResumeEnum.entries.toTypedArray()) {
                            RadioStringBtnView(
                                s = it.nameString(),
                                selected = (it == preferences.resumeEnum),
                                function = {
                                    viewModel.updatePlaybackResume(it)
                                    viewModel.hideDialog()
                                }
                            )
                        }
                    }
                }

                PlayerPreferenceDialog.DoubleTapDialog -> {
                    chooserDialog(
                        header = stringResource(id = R.string.double_tap),
                        onDismissDialog = viewModel::hideDialog
                    ) {
                        items(DoubleTapGestureEnum.entries.toTypedArray()) {
                            RadioStringBtnView(
                                s = it.nameString(),
                                selected = (it == preferences.doubleTapGestureEnum),
                                function = {
                                    viewModel.updateDoubleTapGesture(it)
                                    viewModel.hideDialog()
                                }
                            )
                        }
                    }
                }

                PlayerPreferenceDialog.FastSeekDialog -> {
                    chooserDialog(
                        header = stringResource(id = R.string.fast_seek),
                        onDismissDialog = viewModel::hideDialog
                    ) {
                        items(FastSeek.entries.toTypedArray()) {
                            RadioStringBtnView(
                                s = it.nameString(),
                                selected = (it == preferences.fastSeek),
                                function = {
                                    viewModel.updateFastSeek(it)
                                    viewModel.hideDialog()
                                }
                            )
                        }
                    }
                }

                PlayerPreferenceDialog.PlayerScreenOrientationDialog -> {
                    chooserDialog(
                        header = stringResource(id = R.string.player_screen_orientation),
                        onDismissDialog = viewModel::hideDialog
                    ) {
                        items(ScreenOrientationEnum.entries.toTypedArray()) {
                            RadioStringBtnView(
                                s = it.nameString(),
                                selected = it == preferences.playerScreenOrientationEnum,
                                function = {
                                    viewModel.updatePreferredPlayerOrientation(it)
                                    viewModel.hideDialog()
                                }
                            )
                        }
                    }
                }

                PlayerPreferenceDialog.PlaybackSpeedDialog -> {
                    var defaultPlaybackSpeed by remember {
                        mutableFloatStateOf(preferences.defaultPlaySpeed)
                    }

                    VanillaPlayerDialogWithPositiveAndNagative(
                        title = stringResource(R.string.default_playback_speed),
                        onDoneClick = {
                            viewModel.updateDefaultPlaybackSpeed(defaultPlaybackSpeed)
                            viewModel.hideDialog()
                        },
                        onDismissClick = viewModel::hideDialog,
                        content = {
                            Text(
                                text = "$defaultPlaybackSpeed",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Slider(
                                value = defaultPlaybackSpeed,
                                onValueChange = { defaultPlaybackSpeed = it.roundTheFloat(1) },
                                valueRange = 0.2f..4.0f
                            )
                        }
                    )
                }

                PlayerPreferenceDialog.LongPressControlsSpeedDialog -> {
                    var longPressControlsSpeed by remember {
                        mutableFloatStateOf(preferences.longPressControlsSpeedVal)
                    }

                    VanillaPlayerDialogWithPositiveAndNagative(
                        title = stringResource(R.string.long_press_gesture),
                        onDoneClick = {
                            viewModel.updateLongPressControlsSpeed(longPressControlsSpeed)
                            viewModel.hideDialog()
                        },
                        onDismissClick = viewModel::hideDialog,
                        content = {
                            Text(
                                text = "$longPressControlsSpeed",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Slider(
                                value = longPressControlsSpeed,
                                onValueChange = { longPressControlsSpeed = it.roundTheFloat(1) },
                                valueRange = 0.2f..4.0f
                            )
                        }
                    )
                }

                PlayerPreferenceDialog.ControllerTimeoutDialog -> {
                    var controllerAutoHideSec by remember {
                        mutableIntStateOf(preferences.optionsAutoHideTimeout)
                    }

                    VanillaPlayerDialogWithPositiveAndNagative(
                        title = stringResource(R.string.controller_timeout),
                        onDoneClick = {
                            viewModel.updateControlAutoHideTimeout(controllerAutoHideSec)
                            viewModel.hideDialog()
                        },
                        onDismissClick = viewModel::hideDialog,
                        content = {
                            Text(
                                text = stringResource(R.string.seconds, controllerAutoHideSec),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Slider(
                                value = controllerAutoHideSec.toFloat(),
                                onValueChange = { controllerAutoHideSec = it.toInt() },
                                valueRange = 1.0f..60.0f
                            )
                        }
                    )
                }

                PlayerPreferenceDialog.SeekIncrementDialog -> {
                    var seekIncrement by remember {
                        mutableIntStateOf(preferences.seekIncrementVal)
                    }

                    VanillaPlayerDialogWithPositiveAndNagative(
                        title = stringResource(R.string.seek_increment),
                        onDoneClick = {
                            viewModel.updateSeekIncrement(seekIncrement)
                            viewModel.hideDialog()
                        },
                        onDismissClick = viewModel::hideDialog,
                        content = {
                            Text(
                                text = stringResource(R.string.seconds, seekIncrement),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Slider(
                                value = seekIncrement.toFloat(),
                                onValueChange = { seekIncrement = it.toInt() },
                                valueRange = 1.0f..60.0f
                            )
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun RememberBrightnessSetting(
    isChecked: Boolean,
    onClick: () -> Unit
) {
    VanillaPrefSwitchView(
        title = stringResource(id = R.string.remember_brightness_level),
        description = stringResource(
            id = R.string.remember_brightness_level_description
        ),
        icon = VanillaIcons.Brightness,
        isChecked = isChecked,
        onClick = onClick
    )
}


@Composable
fun FastSeekSetting(
    isChecked: Boolean,
    onChecked: () -> Unit,
    onClick: () -> Unit
) {
    VanillaPrefSwitchWithDividerView(
        s = stringResource(id = R.string.fast_seek),
        desc = stringResource(id = R.string.fast_seek_description),
        isChecked = isChecked,
        checked = onChecked,
        imageVector = VanillaIcons.Fast,
        click = onClick
    )
}


@Composable
fun SeekGestureSetting(
    isChecked: Boolean,
    onClick: () -> Unit
) {
    VanillaPrefSwitchView(
        title = stringResource(id = R.string.seek_gesture),
        description = stringResource(id = R.string.seek_gesture_description),
        icon = VanillaIcons.SwipeHorizontal,
        isChecked = isChecked,
        onClick = onClick
    )
}


@Composable
fun RememberSelectionsSetting(
    isChecked: Boolean,
    onClick: () -> Unit
) {
    VanillaPrefSwitchView(
        title = stringResource(id = R.string.remember_selections),
        description = stringResource(id = R.string.remember_selections_description),
        icon = VanillaIcons.Selection,
        isChecked = isChecked,
        onClick = onClick
    )
}

@Composable
fun LongPressGesture(
    isChecked: Boolean,
    onChecked: () -> Unit,
    playbackSpeed: Float,
    onClick: () -> Unit
) {
    VanillaPrefSwitchWithDividerView(
        s = stringResource(id = R.string.long_press_gesture),
        desc = stringResource(id = R.string.long_press_gesture_desc, playbackSpeed),
        isChecked = isChecked,
        checked = onChecked,
        imageVector = VanillaIcons.Tap,
        click = onClick
    )
}


@Composable
fun ZoomGestureSetting(
    isChecked: Boolean,
    onClick: () -> Unit
) {
    VanillaPrefSwitchView(
        title = stringResource(id = R.string.zoom_gesture),
        description = stringResource(id = R.string.zoom_gesture_description),
        icon = VanillaIcons.Pinch,
        isChecked = isChecked,
        onClick = onClick
    )
}

@Composable
fun SeekIncrementPreference(
    currentValue: Int,
    onClick: () -> Unit
) {
    VanillaClickablePrefItem(
        title = stringResource(R.string.seek_increment),
        description = stringResource(R.string.seconds, currentValue),
        icon = VanillaIcons.Replay,
        onClick = onClick
    )
}


@Composable
fun DoubleTapGestureSetting(
    isChecked: Boolean,
    onChecked: () -> Unit,
    onClick: () -> Unit
) {
    VanillaPrefSwitchWithDividerView(
        s = stringResource(id = R.string.double_tap),
        desc = stringResource(id = R.string.double_tap_description),
        isChecked = isChecked,
        checked = onChecked,
        imageVector = VanillaIcons.DoubleTap,
        click = onClick
    )
}

@Composable
fun ResumeSetting(
    onClick: () -> Unit
) {
    VanillaClickablePrefItem(
        title = stringResource(id = R.string.resume),
        description = stringResource(id = R.string.resume_description),
        icon = VanillaIcons.Resume,
        onClick = onClick
    )
}

@Composable
fun DefaultPlaybackSpeedSetting(
    currentDefaultPlaybackSpeed: Float,
    onClick: () -> Unit
) {
    VanillaClickablePrefItem(
        title = stringResource(id = R.string.default_playback_speed),
        description = currentDefaultPlaybackSpeed.toString(),
        icon = VanillaIcons.Speed,
        onClick = onClick
    )
}


@Composable
fun ControllerTimeoutPreference(
    currentValue: Int,
    onClick: () -> Unit
) {
    VanillaClickablePrefItem(
        title = stringResource(R.string.controller_timeout),
        description = stringResource(R.string.seconds, currentValue),
        icon = VanillaIcons.Timer,
        onClick = onClick
    )
}


@Composable
fun SwipeGestureSetting(
    isChecked: Boolean,
    onClick: () -> Unit
) {
    VanillaPrefSwitchView(
        title = stringResource(id = R.string.swipe_gesture),
        description = stringResource(id = R.string.swipe_gesture_description),
        icon = VanillaIcons.SwipeVertical,
        isChecked = isChecked,
        onClick = onClick
    )
}


@Composable
fun AutoplaySetting(
    isChecked: Boolean,
    onClick: () -> Unit
) {
    VanillaPrefSwitchView(
        title = stringResource(id = R.string.autoplay_settings),
        description = stringResource(
            id = R.string.autoplay_settings_description
        ),
        icon = VanillaIcons.Player,
        isChecked = isChecked,
        onClick = onClick
    )
}


@Composable
fun ScreenOrientationSetting(
    currentOrientationPreference: ScreenOrientationEnum,
    onClick: () -> Unit
) {
    VanillaClickablePrefItem(
        title = stringResource(id = R.string.player_screen_orientation),
        description = currentOrientationPreference.nameString(),
        icon = VanillaIcons.Rotation,
        onClick = onClick
    )
}
