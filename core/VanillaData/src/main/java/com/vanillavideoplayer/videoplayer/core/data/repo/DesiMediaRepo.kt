package com.vanillavideoplayer.videoplayer.core.data.repo

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.vanillavideoplayer.videoplayer.core.common.commonobj.ApplicationScope
import com.vanillavideoplayer.videoplayer.core.common.extra.deleteFilesFromUris
import com.vanillavideoplayer.videoplayer.core.data.model.VidState
import com.vanillavideoplayer.videoplayer.core.data.x.toFolder
import com.vanillavideoplayer.videoplayer.core.data.x.toVideo
import com.vanillavideoplayer.videoplayer.core.data.x.toVideoState
import com.vanillavideoplayer.videoplayer.core.database.converter.UriListConverterObj
import com.vanillavideoplayer.videoplayer.core.database.dao.DirDaoInter
import com.vanillavideoplayer.videoplayer.core.database.dao.MediumDaoInter
import com.vanillavideoplayer.videoplayer.core.database.dataclass.DirEntityData
import com.vanillavideoplayer.videoplayer.core.database.dataclass.MediumEntityData
import com.vanillavideoplayer.videoplayer.core.model.FolderData
import com.vanillavideoplayer.videoplayer.core.model.VideoData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class DesiMediaRepo @Inject constructor(
    private val mediumDaoInter: MediumDaoInter,
    private val dirDaoInter: DirDaoInter,
    @ApplicationContext private val context: Context,
    @ApplicationScope private val applicationScope: CoroutineScope
) : MediaRepo {

    override suspend fun saveLastPlayedState(path: String, lastPlayed: Long?) {
        applicationScope.launch {
            mediumDaoInter.changeLastPlayedState(
                path = path,
                lastPlayed = lastPlayed ?: 0L
            )
        }
    }

    override fun getVideosFlow(): Flow<List<VideoData>> {
        return mediumDaoInter.fetchAll().map { it.map(MediumEntityData::toVideo) }
    }

    override suspend fun getVideoState(path: String): VidState? {
        return mediumDaoInter.fetch(path)?.toVideoState()
    }

    override fun getVideosFlowFromFolderPath(folderPath: String): Flow<List<VideoData>> {
        return mediumDaoInter.fetchAllFromDir(folderPath).map { it.map(MediumEntityData::toVideo) }
    }

    override suspend fun deleteVideos(videoUris: List<String>, intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>) {

        val mediaUrisToDelete = videoUris.map { Uri.parse(it) }
        context.deleteFilesFromUris(mediaUrisToDelete, intentSenderLauncher)
    }


    override fun getFoldersFlow(): Flow<List<FolderData>> {
        return dirDaoInter.fetchAll().map { it.map(DirEntityData::toFolder) }
    }

    override suspend fun saveVideoState(
        path: String,
        position: Long,
        audioTrackIndex: Int?,
        subtitleTrackIndex: Int?,
        playbackSpeed: Float?,
        externalSubs: List<Uri>,
        lastPlayed: Long?
    ) {
        Timber.d(
            "save state for [$path]: [$position, $audioTrackIndex, $subtitleTrackIndex, $playbackSpeed]"
        )

        applicationScope.launch {
            mediumDaoInter.changeMediumState(
                path = path,
                position = position,
                audioTrackIndex = audioTrackIndex,
                subtitleTrackIndex = subtitleTrackIndex,
                playbackSpeed = playbackSpeed,
                externalSubs = UriListConverterObj.proceedFromListToString(externalSubs),
                lastPlayed = lastPlayed ?: 0L
            )
        }
    }


    override suspend fun deleteFolders(folderPaths: List<String>, intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>) {
        val mediumEntitiesToDelete = mutableListOf<MediumEntityData>()
        for (path in folderPaths) {
            mediumEntitiesToDelete += mediumDaoInter.fetchAllFromDir(path).first()
        }
        val mediaUrisToDelete = mediumEntitiesToDelete.map { Uri.parse(it.uriString) }
        context.deleteFilesFromUris(mediaUrisToDelete, intentSenderLauncher)
    }


}
