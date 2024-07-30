package com.vanillavideoplayer.videoplayer.core.data.x

import com.vanillavideoplayer.videoplayer.core.common.CommonAi
import com.vanillavideoplayer.videoplayer.core.data.model.VidState
import com.vanillavideoplayer.videoplayer.core.database.converter.UriListConverterObj
import com.vanillavideoplayer.videoplayer.core.database.dataclass.DirEntityData
import com.vanillavideoplayer.videoplayer.core.database.dataclass.MediumEntityData
import com.vanillavideoplayer.videoplayer.core.model.FolderData
import com.vanillavideoplayer.videoplayer.core.model.VideoData

fun MediumEntityData.toVideoState(): VidState {
    return VidState(
        path = path,
        position = playbackPosition,
        audioTrackIndex = audioTrackIndex,
        subtitleTrackIndex = subtitleTrackIndex,
        playbackSpeed = playbackSpeed,
        externalSubs = UriListConverterObj.proceedFromStringToList(externalSubs),
        lastPlayed = lastPlayed
    )
}

fun DirEntityData.toFolder() = FolderData(
    name = name,
    path = path,
    mediaSize = size,
    mediaCount = mediaCount,
    dateModified = modified,
    formattedMediaSize = CommonAi.returnFormattedFileSize(size)
)

fun MediumEntityData.toVideo() = VideoData(
    id = mediaStoreId,
    path = path,
    parentPath = parentPath,
    duration = duration,
    uriString = uriString,
    displayName = name.substringBeforeLast("."),
    nameWithExtension = name,
    width = width,
    height = height,
    size = size,
    dateModified = modified,
    formattedDuration = CommonAi.formatDurationInHours(duration),
    formattedFileSize = CommonAi.returnFormattedFileSize(size),
    lastPlayed = lastPlayed
)
