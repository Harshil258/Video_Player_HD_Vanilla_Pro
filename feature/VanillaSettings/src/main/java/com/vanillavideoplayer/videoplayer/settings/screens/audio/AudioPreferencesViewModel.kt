package com.vanillavideoplayer.videoplayer.settings.screens.audio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanillavideoplayer.videoplayer.core.data.repo.PrefRepo
import com.vanillavideoplayer.videoplayer.core.model.PlayerPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioPreferencesViewModel @Inject constructor(
    private val prefRepo: PrefRepo
) : ViewModel() {

    val preferencesFlow = prefRepo.playerPref.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = PlayerPref()
    )

    private val _uiState = MutableStateFlow(AudioPreferencesUIState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: AudioPreferencesEvent) {
        if (event is AudioPreferencesEvent.ShowDialog) {
            _uiState.update {
                it.copy(showDialog = event.value)
            }
        }
    }

    fun updateAudioLanguage(value: String) {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(suggestedAudioLang = value)
            }
        }
    }

    fun togglePauseOnHeadsetDisconnect() {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(boolPauseOnHeadsetDisconnect = !it.boolPauseOnHeadsetDisconnect)
            }
        }
    }

    fun toggleShowSystemVolumePanel() {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(systemVolumePanelDekhadu = !it.systemVolumePanelDekhadu)
            }
        }
    }

    fun toggleRequireAudioFocus() {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(audioFocusNiJarurChheKeNai = !it.audioFocusNiJarurChheKeNai)
            }
        }
    }
}

data class AudioPreferencesUIState(
    val showDialog: AudioPreferenceDialog? = null
)

fun AudioPreferencesViewModel.showDialog(dialog: AudioPreferenceDialog) {
    onEvent(AudioPreferencesEvent.ShowDialog(dialog))
}

fun AudioPreferencesViewModel.hideDialog() {
    onEvent(AudioPreferencesEvent.ShowDialog(null))
}

sealed interface AudioPreferenceDialog {
    object AudioLanguageDialog : AudioPreferenceDialog
}

sealed interface AudioPreferencesEvent {
    data class ShowDialog(val value: AudioPreferenceDialog?) : AudioPreferencesEvent
}