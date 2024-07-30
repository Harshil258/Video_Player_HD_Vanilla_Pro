package com.vanillavideoplayer.videoplayer.feature.player

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import com.vanillavideoplayer.videoplayer.core.data.model.VidState
import com.vanillavideoplayer.videoplayer.core.data.repo.MediaRepo
import com.vanillavideoplayer.videoplayer.core.data.repo.PrefRepo
import com.vanillavideoplayer.videoplayer.core.domain.FetchSortedPlaylistUC
import com.vanillavideoplayer.videoplayer.core.model.ApplicationPrefData
import com.vanillavideoplayer.videoplayer.core.model.PlayerPref
import com.vanillavideoplayer.videoplayer.core.model.ResumeEnum
import com.vanillavideoplayer.videoplayer.core.model.VidZoomEnum
import com.vanillavideoplayer.videoplayer.core.ui.PlaylistKeeper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val END_POSITION_OFFSET = 5L

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val mediaRepo: MediaRepo,
    private val prefRepo: PrefRepo,
    private val fetchSortedPlaylistUC: FetchSortedPlaylistUC,
) : ViewModel() {

    var currentPlaybackPosition: Long? = null
    var currentPlaybackSpeed: Float = 1f
    var currentAudioTrackIndex: Int? = null
    var currentSubtitleTrackIndex: Int? = null
    var isPlaybackSpeedChanged: Boolean = false
    val externalSubtitles = mutableSetOf<Uri>()

    private var currentVidState: VidState? = null

    val playerPrefs = prefRepo.playerPref.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = PlayerPref()
    )

    val appPrefsData = prefRepo.applicationPrefData.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ApplicationPrefData()
    )

    suspend fun updateState(path: String?) {
        currentVidState = path?.let { mediaRepo.getVideoState(it) }

        Timber.d("$currentVidState")

        val prefs = playerPrefs.value
        currentPlaybackPosition = currentVidState?.position.takeIf { prefs.resumeEnum == ResumeEnum.RESUME_YES }
            ?: currentPlaybackPosition
        currentAudioTrackIndex =
            currentVidState?.audioTrackIndex.takeIf { prefs.saveSelections }
                ?: currentAudioTrackIndex
        currentSubtitleTrackIndex =
            currentVidState?.subtitleTrackIndex.takeIf { prefs.saveSelections }
                ?: currentSubtitleTrackIndex
        currentPlaybackSpeed = currentVidState?.playbackSpeed.takeIf { prefs.saveSelections }
            ?: prefs.defaultPlaySpeed
        externalSubtitles += currentVidState?.externalSubs ?: emptyList()
    }

    suspend fun getVideoState(path: String?): VidState? {
        return path?.let { mediaRepo.getVideoState(it) }
    }

    suspend fun getPlaylistFromUri(uri: Uri): List<Uri> {
        return PlaylistKeeper.getPlaylistFromUri()
//        return getSortedPlaylistUseCase.invoke(uri)
    }

    fun saveLastPlayedState(
        path: String?,
        lastPlayed: Long,
    ) {
        viewModelScope.launch {
            path?.let {
                mediaRepo.saveLastPlayedState(
                    path = it,
                    lastPlayed = lastPlayed
                )
            }
        }
    }

    fun saveState(
        path: String?,
        position: Long,
        duration: Long,
        audioTrackIndex: Int,
        subtitleTrackIndex: Int,
        playbackSpeed: Float,
        lastPlayed: Long,
    ) {
        currentPlaybackPosition = position
        currentAudioTrackIndex = audioTrackIndex
        currentSubtitleTrackIndex = subtitleTrackIndex
        currentPlaybackSpeed = playbackSpeed

        if (path == null) return

        val newPosition = position.takeIf {
            position < duration - END_POSITION_OFFSET
        } ?: C.TIME_UNSET

        viewModelScope.launch {
            mediaRepo.saveVideoState(
                path = path,
                position = newPosition,
                audioTrackIndex = audioTrackIndex,
                subtitleTrackIndex = subtitleTrackIndex,
                playbackSpeed = playbackSpeed.takeIf { isPlaybackSpeedChanged }
                    ?: currentVidState?.playbackSpeed,
                externalSubs = externalSubtitles.toList(),
                lastPlayed = lastPlayed
            )
        }
    }

    fun setPlayerBrightness(value: Float) {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences { it.copy(playerBright = value) }
        }
    }

    fun setVideoZoom(vidZoomEnum: VidZoomEnum) {
        viewModelScope.launch {
            prefRepo.updatePlayerPreferences { it.copy(playerVidZoomEnum = vidZoomEnum) }
        }
    }

    fun resetAllToDefaults() {
        currentPlaybackPosition = null
        currentPlaybackSpeed = 1f
        currentAudioTrackIndex = null
        currentSubtitleTrackIndex = null
        isPlaybackSpeedChanged = false
        externalSubtitles.clear()
    }
}
