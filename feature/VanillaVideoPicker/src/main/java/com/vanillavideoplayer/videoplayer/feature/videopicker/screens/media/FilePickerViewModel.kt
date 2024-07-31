package com.vanillavideoplayer.videoplayer.feature.videopicker.screens.media

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vanillavideoplayer.videoplayer.core.data.repo.MediaRepo
import com.vanillavideoplayer.videoplayer.core.data.repo.PrefRepo
import com.vanillavideoplayer.videoplayer.core.domain.FetchSortedFoldersUC
import com.vanillavideoplayer.videoplayer.core.domain.FetchSortedVidUC
import com.vanillavideoplayer.videoplayer.core.model.ApplicationPrefData
import com.vanillavideoplayer.videoplayer.core.model.VideoData
import com.vanillavideoplayer.videoplayer.feature.videopicker.screens.FoldersStateSealedInter
import com.vanillavideoplayer.videoplayer.feature.videopicker.screens.VideosStateSealedInter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilePickerViewModel @Inject constructor(
    fetchSortedVidUC: FetchSortedVidUC,
    getSortedFoldersUseCase: FetchSortedFoldersUC,
    private val mediaRepo: MediaRepo,
    private val prefRepo: PrefRepo,
) : ViewModel() {

    val videosStateSealedInter =
        fetchSortedVidUC.invoke().map { VideosStateSealedInter.SuccessDataClass(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = VideosStateSealedInter.LoadingDataObj
        )

    val recentVideoState = mediaRepo.getVideosFlow().map { videoList ->
        VideosStateSealedInter.SuccessDataClass(videoList.filter { it.lastPlayed != null && it.lastPlayed != 0L }
            .sortedByDescending { it.lastPlayed })
    }.stateIn(
        scope = viewModelScope, // Use the appropriate scope
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = VideosStateSealedInter.LoadingDataObj
    )

    val dirsStateSealedInter =
        getSortedFoldersUseCase.invoke().map { FoldersStateSealedInter.Success(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FoldersStateSealedInter.LoadingDataOb
        )

    val prefs = prefRepo.applicationPrefData.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ApplicationPrefData()
    )

    fun initMenu(applicationPrefData: ApplicationPrefData) {
        viewModelScope.launch {
            prefRepo.updateApplicationPreferences { applicationPrefData }
        }
    }

    fun removeVideos(
        uris: List<String>,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>,
    ) {

        viewModelScope.launch {
            mediaRepo.deleteVideos(uris, intentSenderLauncher)
        }
    }

    fun removeDirs(
        paths: List<String>,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>,
    ) {
        viewModelScope.launch {
            mediaRepo.deleteFolders(paths, intentSenderLauncher)
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
