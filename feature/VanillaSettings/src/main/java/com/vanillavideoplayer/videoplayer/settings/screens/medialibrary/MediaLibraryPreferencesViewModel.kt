package com.vanillavideoplayer.videoplayer.settings.screens.medialibrary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanillavideoplayer.videoplayer.core.data.repo.MediaRepo
import com.vanillavideoplayer.videoplayer.core.data.repo.PrefRepo
import com.vanillavideoplayer.videoplayer.core.model.ApplicationPrefData
import com.vanillavideoplayer.videoplayer.core.model.FolderData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed interface FolderPreferencesUiState {
    object Loading : FolderPreferencesUiState

    data class Success(val directories: List<FolderData>) : FolderPreferencesUiState
}

@HiltViewModel
class MediaLibraryPreferencesViewModel @Inject constructor(
    mediaRepo: MediaRepo,
    private val prefRepo: PrefRepo
) : ViewModel() {

    val uiState = mediaRepo.getFoldersFlow()
        .map { FolderPreferencesUiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FolderPreferencesUiState.Loading
        )

    val preferences = prefRepo.applicationPrefData
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ApplicationPrefData()
        )

    fun updateExcludeList(path: String) {
        viewModelScope.launch {
            prefRepo.updateApplicationPreferences {
                it.copy(
                    excludeFolders = if (path in it.excludeFolders) {
                        it.excludeFolders - path
                    } else {
                        it.excludeFolders + path
                    }
                )
            }
        }
    }
}
