package com.vanillavideoplayer.hd.videoplayer.pro.settings.screens.subtitle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanillavideoplayer.hd.videoplayer.pro.core.data.repo.PrefRepo
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.FontEnum
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.PlayerPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

fun SubtitlePrefVM.displayDialog(dialog: SubtitlePrefDialog) {
    onEvent(SubtitlePrefInter.SubPrefInterData(dialog))
}

fun SubtitlePrefVM.dismissDialog() {
    onEvent(SubtitlePrefInter.SubPrefInterData(null))
}

@HiltViewModel
class SubtitlePrefVM @Inject constructor(
    private val prefRepo: PrefRepo
) : ViewModel() {

    val prefFlow = prefRepo.playerPref.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = PlayerPref()
    )

    private val _uiLevelData = MutableStateFlow(SubtitlePrefUIState())
    val uiLevel = _uiLevelData.asStateFlow()

    fun onEvent(event: SubtitlePrefInter) {
        if (event is SubtitlePrefInter.SubPrefInterData) {
            _uiLevelData.update {
                it.copy(subPrefDialog = event.value)
            }
        }
    }

    fun changeSubtitleLanguage(value: String) {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(suggestedSubtitleLang = value)
            }
        }
    }

    fun changeSubtitleFont(value: FontEnum) {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(subtitleFontEnum = value)
            }
        }
    }

    fun switchSubtitleToBold() {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(boolSubtitleTextBold = !it.boolSubtitleTextBold)
            }
        }
    }

    fun changeSubtitleFontSize(value: Int) {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(subtitleTextSizeVal = value)
            }
        }
    }

    fun switchSubtitleBackground() {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(boolSubtitleBackground = !it.boolSubtitleBackground)
            }
        }
    }

    fun switchApplyEmbeddedStyles() {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences {
                it.copy(boolApplyEmbeddedStyles = !it.boolApplyEmbeddedStyles)
            }
        }
    }

    fun changeSubtitleEncoding(value: String) {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences { it.copy(subtitleTextEncodingVal = value) }
        }
    }

    fun switchUseSystemCaptionStyle() {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences { it.copy(boolSystemCaptionStyle = !it.boolSystemCaptionStyle) }
        }
    }
}

data class SubtitlePrefUIState(
    val subPrefDialog: SubtitlePrefDialog? = null
)

sealed interface SubtitlePrefDialog {
    object SubLangDialog : SubtitlePrefDialog
    object SubFontDialog : SubtitlePrefDialog
    object SubSizeDialog : SubtitlePrefDialog
    object SubEncodingDialog : SubtitlePrefDialog
}

sealed interface SubtitlePrefInter {
    data class SubPrefInterData(val value: SubtitlePrefDialog?) : SubtitlePrefInter
}
