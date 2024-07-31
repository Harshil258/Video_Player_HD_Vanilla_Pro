package com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.screens.mediaFolder

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanillavideoplayer.hd.videoplayer.pro.core.data.repo.MediaRepo
import com.vanillavideoplayer.hd.videoplayer.pro.core.data.repo.PrefRepo
import com.vanillavideoplayer.hd.videoplayer.pro.core.domain.FetchSortedVidUC
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.ApplicationPrefData
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.VideoData
import com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.navigation.DirArgs
import com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.screens.VideosStateSealedInter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderPickerViewModel @Inject constructor(
    fetchSortedVidUC: FetchSortedVidUC, savedStateHandle: SavedStateHandle, private val mediaRepo: MediaRepo, prefRepo: PrefRepo
) : ViewModel() {

    private val dirArgs = DirArgs(savedStateHandle)

    val dirPath = dirArgs.folderId

    val videos = fetchSortedVidUC.invoke(dirPath)
        .map { VideosStateSealedInter.SuccessDataClass(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = VideosStateSealedInter.LoadingDataObj
        )


    val pref = prefRepo.applicationPrefData
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ApplicationPrefData()
        )

    fun removeVideos(
        uris: List<String>,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {


        viewModelScope.launch {
            mediaRepo.deleteVideos(uris, intentSenderLauncher)
        }
    }


    private val _selectedTracks = mutableStateListOf<VideoData>()
    val selectedTracks: List<VideoData> get() = _selectedTracks

    var videoTracks = listOf<VideoData>()

    fun addToSelectedTracks(track: VideoData) {
        _selectedTracks.add(track)

    }

    fun removeFromSelectedTracks(track: VideoData) {
        _selectedTracks.remove(track)
    }

    fun clearSelectedTracks() {
        _selectedTracks.forEach {
            it.isSelected = false
        }
        _selectedTracks.clear()
    }

}
