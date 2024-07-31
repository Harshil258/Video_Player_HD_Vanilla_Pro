package com.vanillavideoplayer.hd.videoplayer.pro.settings.screens.decoder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanillavideoplayer.hd.videoplayer.pro.core.data.repo.PrefRepo
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.DecoderPriority
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.PlayerPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

fun DecoderPreferencesViewModel.hideDialog() {
    onEvent(DecoderPreferencesEvent.ShowDialog(null))
}

@HiltViewModel
class DecoderPreferencesViewModel @Inject constructor(
    private val prefRepo: PrefRepo
) : ViewModel() {

    val preferencesFlow = prefRepo.playerPref
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = PlayerPref()
        )

    private val _uiState = MutableStateFlow(DecoderPreferencesUIState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: DecoderPreferencesEvent) {
        if (event is DecoderPreferencesEvent.ShowDialog) {
            _uiState.update {
                it.copy(showDialog = event.value)
            }
        }
    }

    fun updateDecoderPriority(value: DecoderPriority) {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(decoderPriority = value)
            }
        }
    }
}

data class DecoderPreferencesUIState(
    val showDialog: DecoderPreferenceDialog? = null
)

fun DecoderPreferencesViewModel.showDialog(dialog: DecoderPreferenceDialog) {
    onEvent(DecoderPreferencesEvent.ShowDialog(dialog))
}


sealed interface DecoderPreferenceDialog {
    object DecoderPriorityDialog : DecoderPreferenceDialog
}

sealed interface DecoderPreferencesEvent {
    data class ShowDialog(val value: DecoderPreferenceDialog?) : DecoderPreferencesEvent
}