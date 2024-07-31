package com.vanillavideoplayer.hd.videoplayer.pro.core.data.repo

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.vanillavideoplayer.hd.videoplayer.pro.core.data.model.VidState
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.FolderData
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.VideoData
import kotlinx.coroutines.flow.Flow

interface MediaRepo {

    fun getVideosFlow(): Flow<List<VideoData>>

    suspend fun saveVideoState(
        path: String,
        position: Long,
        audioTrackIndex: Int?,
        subtitleTrackIndex: Int?,
        playbackSpeed: Float?,
        externalSubs: List<Uri>,
        lastPlayed: Long?,
    )

    fun getVideosFlowFromFolderPath(folderPath: String): Flow<List<VideoData>>
    suspend fun saveLastPlayedState(
        path: String,
        lastPlayed: Long?,
    )

    suspend fun deleteVideos(
        videoUris: List<String>,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>,
    )


    fun getFoldersFlow(): Flow<List<FolderData>>


    suspend fun getVideoState(path: String): VidState?


    suspend fun deleteFolders(
        folderPaths: List<String>,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>,
    )
}
