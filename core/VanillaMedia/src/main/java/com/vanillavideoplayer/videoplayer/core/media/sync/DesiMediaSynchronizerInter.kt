package com.vanillavideoplayer.videoplayer.core.media.sync

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.provider.MediaStore
import com.vanillavideoplayer.videoplayer.core.common.Dispatcher
import com.vanillavideoplayer.videoplayer.core.common.VideoPlayerDispatchers
import com.vanillavideoplayer.videoplayer.core.common.commonobj.ApplicationScope
import com.vanillavideoplayer.videoplayer.core.common.extra.VIDEO_CONTENT_URI
import com.vanillavideoplayer.videoplayer.core.common.extra.prettyName
import com.vanillavideoplayer.videoplayer.core.database.converter.UriListConverterObj
import com.vanillavideoplayer.videoplayer.core.database.dao.DirDaoInter
import com.vanillavideoplayer.videoplayer.core.database.dao.MediumDaoInter
import com.vanillavideoplayer.videoplayer.core.database.dataclass.DirEntityData
import com.vanillavideoplayer.videoplayer.core.database.dataclass.MediumEntityData
import com.vanillavideoplayer.videoplayer.core.media.model.MediaVidModelData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class DesiMediaSynchronizerInter @Inject constructor(
    private val mediumDaoInter: MediumDaoInter,
    private val dirDaoInter: DirDaoInter,
    @ApplicationScope private val applicationScope: CoroutineScope,
    @ApplicationContext private val context: Context,
    @Dispatcher(VideoPlayerDispatchers.IO) private val dispatcher: CoroutineDispatcher
) : MediaSynchronizerInter {


    override fun stopSynchronizing() {
        mediaSynchronzingJob?.cancel()
    }

    private var mediaSynchronzingJob: Job? = null

    override fun startSynchronizing() {
        if (mediaSynchronzingJob != null) return
        mediaSynchronzingJob = fetchMediaVidsFlow().onEach { media ->
            applicationScope.launch { invalidateDirectories(media) }
            applicationScope.launch { invalidateMedia(media) }
        }.launchIn(applicationScope)
    }

    private suspend fun invalidateMedia(media: List<MediaVidModelData>) = withContext(Dispatchers.Default) {
        val mediumEntities = media.map {
            val file = File(it.data)
            val mediumEntity = mediumDaoInter.fetch(it.data)
            mediumEntity?.copy(
                uriString = it.uri.toString(),
                modified = it.dateModified,
                size = it.size,
                width = it.width,
                height = it.height,
                duration = it.duration,
                mediaStoreId = it.id
            ) ?: MediumEntityData(
                path = it.data,
                uriString = it.uri.toString(),
                name = file.name,
                parentPath = file.parent!!,
                modified = it.dateModified,
                size = it.size,
                width = it.width,
                height = it.height,
                duration = it.duration,
                mediaStoreId = it.id
            )
        }

        mediumDaoInter.addAll(mediumEntities)

        val currentMediaPaths = mediumEntities.map { it.path }

        val unwantedMedia = mediumDaoInter.fetchAll().first()
            .filterNot { it.path in currentMediaPaths }

        val unwantedMediaPaths = unwantedMedia.map { it.path }

        mediumDaoInter.remove(unwantedMediaPaths)

        // Release external subtitle uri permission if not used by any other media
        launch {
            val currentMediaExternalSubs = mediumEntities.flatMap { UriListConverterObj.proceedFromStringToList(it.externalSubs) }.toSet()

            unwantedMedia.onEach {
                for (sub in UriListConverterObj.proceedFromStringToList(it.externalSubs)) {
                    if (sub !in currentMediaExternalSubs) {
                        context.contentResolver.releasePersistableUriPermission(sub, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                }
            }
        }
    }

    private fun getMediaVid(
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): List<MediaVidModelData> {
        val mediaVidModelData = mutableListOf<MediaVidModelData>()
        context.contentResolver.query(
            VIDEO_CONTENT_URI,
            VID_PROJECTION_VAL,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->

            val idColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID)
            val dataColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATA)
            val durationColumn = cursor.getColumnIndex(MediaStore.Video.Media.DURATION)
            val widthColumn = cursor.getColumnIndex(MediaStore.Video.Media.WIDTH)
            val heightColumn = cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT)
            val sizeColumn = cursor.getColumnIndex(MediaStore.Video.Media.SIZE)
            val dateModifiedColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                mediaVidModelData.add(
                    MediaVidModelData(
                        id = id,
                        data = cursor.getString(dataColumn),
                        duration = cursor.getLong(durationColumn),
                        uri = ContentUris.withAppendedId(VIDEO_CONTENT_URI, id),
                        width = cursor.getInt(widthColumn),
                        height = cursor.getInt(heightColumn),
                        size = cursor.getLong(sizeColumn),
                        dateModified = cursor.getLong(dateModifiedColumn)
                    )
                )
            }
        }
        return mediaVidModelData.filter { File(it.data).exists() }
    }

    private fun fetchMediaVidsFlow(
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sortOrder: String? = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"
    ): Flow<List<MediaVidModelData>> = callbackFlow {
        val observer = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                trySend(getMediaVid(selection, selectionArgs, sortOrder))
            }
        }
        context.contentResolver.registerContentObserver(VIDEO_CONTENT_URI, true, observer)
        // initial value
        trySend(getMediaVid(selection, selectionArgs, sortOrder))
        // close
        awaitClose { context.contentResolver.unregisterContentObserver(observer) }
    }.flowOn(dispatcher).distinctUntilChanged()

    private suspend fun invalidateDirectories(media: List<MediaVidModelData>) = withContext(
        Dispatchers.Default
    ) {
        val directories = media.groupBy { File(it.data).parentFile!! }.map { (file, videos) ->
            DirEntityData(
                path = file.path,
                name = file.prettyName,
                mediaCount = videos.size,
                size = videos.sumOf { it.size },
                modified = file.lastModified()
            )
        }
        dirDaoInter.addAll(directories)

        val currentDirectoryPaths = directories.map { it.path }

        val unwantedDirectories = dirDaoInter.fetchAll().first()
            .filterNot { it.path in currentDirectoryPaths }

        val unwantedDirectoriesPaths = unwantedDirectories.map { it.path }

        dirDaoInter.remove(unwantedDirectoriesPaths)
    }

    companion object {
        val VID_PROJECTION_VAL = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_MODIFIED
        )
    }
}
