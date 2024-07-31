package com.vanillavideoplayer.hd.videoplayer.pro.settings.screens.appearance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanillavideoplayer.hd.videoplayer.pro.core.data.repo.PrefRepo
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.ApplicationPrefData
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.Size
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.ThemeConfigEnum
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.VideoTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppearancePreferencesViewModel @Inject constructor(
    private val prefRepo: PrefRepo
) : ViewModel() {
    val preferencesFlow = prefRepo.applicationPrefData.stateIn(
        scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = ApplicationPrefData()
    )

    private val _uiState = MutableStateFlow(AppearancePreferencesUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: AppearancePreferencesEvent) {
        if (event is AppearancePreferencesEvent.ShowDialog) {
            _uiState.update {
                it.copy(showDialog = event.value)
            }
        }
    }

    fun toggleDarkTheme() {
        viewModelScope.launch {
            prefRepo.updateApplicationPreferences {
                it.copy(
                    themeConfigEnum = if (it.themeConfigEnum == ThemeConfigEnum.THEME_ON) ThemeConfigEnum.THEME_OFF else ThemeConfigEnum.THEME_ON
                )
            }
        }
    }

    fun updateThemeConfig(themeConfigEnum: ThemeConfigEnum) {
        viewModelScope.launch {
            prefRepo.updateApplicationPreferences {
                it.copy(themeConfigEnum = themeConfigEnum)
            }
        }
    }

    fun toggleUseDynamicColors() {
        viewModelScope.launch {
            prefRepo.updateApplicationPreferences {
                it.copy(useDynamicColors = !it.useDynamicColors)
            }
        }
    }

    fun toggleChangeThumbnailSize() {
        viewModelScope.launch {
            prefRepo.updateApplicationPreferences {
                it.copy(
                    showVideoTheme = if (it.showVideoTheme == VideoTheme.THUMBNAIL_MEDIUM) {
                        VideoTheme.THUMBNAIL_BIG
                    } else {
                        VideoTheme.THUMBNAIL_MEDIUM
                    }
                )
            }
        }
    }

    fun toggleUseHighContrastDarkTheme() {
        viewModelScope.launch {
            prefRepo.updateApplicationPreferences {
                it.copy(useHighContrastDarkTheme = !it.useHighContrastDarkTheme)
            }
        }
    }

    fun updateThumbnailSize(size: Size) {
        viewModelScope.launch {
            prefRepo.updateApplicationPreferences {
                it.copy(thumbnailSize = size)
            }
        }
    }
}

data class AppearancePreferencesUiState(
    val showDialog: AppearancePreferenceDialog? = null
)

sealed interface AppearancePreferencesEvent {
    data class ShowDialog(val value: AppearancePreferenceDialog?) : AppearancePreferencesEvent
}

sealed interface AppearancePreferenceDialog {
    object Theme : AppearancePreferenceDialog
    object ThumbnailSize : AppearancePreferenceDialog
}

fun AppearancePreferencesViewModel.showDialog(dialog: AppearancePreferenceDialog) {
    onEvent(AppearancePreferencesEvent.ShowDialog(dialog))
}

fun AppearancePreferencesViewModel.hideDialog() {
    onEvent(AppearancePreferencesEvent.ShowDialog(null))
}
