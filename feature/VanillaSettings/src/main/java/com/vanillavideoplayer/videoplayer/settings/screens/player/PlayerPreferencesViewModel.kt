package com.vanillavideoplayer.videoplayer.settings.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanillavideoplayer.videoplayer.core.data.repo.PrefRepo
import com.vanillavideoplayer.videoplayer.core.model.DoubleTapGestureEnum
import com.vanillavideoplayer.videoplayer.core.model.FastSeek
import com.vanillavideoplayer.videoplayer.core.model.PlayerPref
import com.vanillavideoplayer.videoplayer.core.model.ResumeEnum
import com.vanillavideoplayer.videoplayer.core.model.ScreenOrientationEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerPreferencesViewModel @Inject constructor(
    private val prefRepo: PrefRepo
) : ViewModel() {

    val preferencesFlow = prefRepo.playerPref.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = PlayerPref()
    )

    private val _uiState = MutableStateFlow(PlayerPreferencesUIState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: PlayerPreferencesEvent) {
        if (event is PlayerPreferencesEvent.ShowDialog) {
            _uiState.update {
                it.copy(showDialog = event.value)
            }
        }
    }

    fun updatePlaybackResume(resumeEnum: ResumeEnum) {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(
                    resumeEnum = resumeEnum
                )
            }
        }
    }

    fun updateDoubleTapGesture(gesture: DoubleTapGestureEnum) {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(doubleTapGestureEnum = gesture)
            }
        }
    }

    fun updateFastSeek(fastSeek: FastSeek) {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(fastSeek = fastSeek)
            }
        }
    }

    fun toggleUseLongPressControls() {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(boolLongPressControls = !it.boolLongPressControls)
            }
        }
    }

    fun toggleDoubleTapGesture() {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(
                    doubleTapGestureEnum = if (it.doubleTapGestureEnum == DoubleTapGestureEnum.DTG_NONE) {
                        DoubleTapGestureEnum.DTG_FAST_FORWARD_AND_REWIND
                    } else {
                        DoubleTapGestureEnum.DTG_NONE
                    }
                )
            }
        }
    }

    fun toggleFastSeek() {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(
                    fastSeek = if (it.fastSeek == FastSeek.DISABLE) FastSeek.AUTO else FastSeek.DISABLE
                )
            }
        }
    }

    fun toggleAutoplay() {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(boolAutoplay = !it.boolAutoplay)
            }
        }
    }

    fun toggleRememberBrightnessLevel() {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(savePlayerBright = !it.savePlayerBright)
            }
        }
    }

    fun toggleUseSwipeControls() {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(boolSwipeControls = !it.boolSwipeControls)
            }
        }
    }

    fun toggleUseSeekControls() {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(boolSeekControls = !it.boolSeekControls)
            }
        }
    }

    fun toggleUseZoomControls() {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(boolZoomControls = !it.boolZoomControls)
            }
        }
    }

    fun toggleRememberSelections() {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(saveSelections = !it.saveSelections)
            }
        }
    }

    fun updatePreferredPlayerOrientation(value: ScreenOrientationEnum) {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(playerScreenOrientationEnum = value)
            }
        }
    }

    fun updateDefaultPlaybackSpeed(value: Float) {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(defaultPlaySpeed = value)
            }
        }
    }

    fun updateLongPressControlsSpeed(value: Float) {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences { it.copy(longPressControlsSpeedVal = value) }
        }
    }

    fun updateControlAutoHideTimeout(value: Int) {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(optionsAutoHideTimeout = value)
            }
        }
    }

    fun updateSeekIncrement(value: Int) {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(seekIncrementVal = value)
            }
        }
    }
}

data class PlayerPreferencesUIState(
    val showDialog: PlayerPreferenceDialog? = null
)

fun PlayerPreferencesViewModel.showDialog(dialog: PlayerPreferenceDialog) {
    onEvent(PlayerPreferencesEvent.ShowDialog(dialog))
}


sealed interface PlayerPreferenceDialog {
    object ResumeDialog : PlayerPreferenceDialog
    object DoubleTapDialog : PlayerPreferenceDialog
    object FastSeekDialog : PlayerPreferenceDialog
    object PlayerScreenOrientationDialog : PlayerPreferenceDialog
    object PlaybackSpeedDialog : PlayerPreferenceDialog
    object LongPressControlsSpeedDialog : PlayerPreferenceDialog
    object ControllerTimeoutDialog : PlayerPreferenceDialog
    object SeekIncrementDialog : PlayerPreferenceDialog
}

fun PlayerPreferencesViewModel.hideDialog() {
    onEvent(PlayerPreferencesEvent.ShowDialog(null))
}

sealed interface PlayerPreferencesEvent {
    data class ShowDialog(val value: PlayerPreferenceDialog?) : PlayerPreferencesEvent
}

