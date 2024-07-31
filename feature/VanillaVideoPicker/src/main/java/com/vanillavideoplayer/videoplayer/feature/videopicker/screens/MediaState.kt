package com.vanillavideoplayer.videoplayer.feature.videopicker.screens

import com.vanillavideoplayer.videoplayer.core.model.FolderData
import com.vanillavideoplayer.videoplayer.core.model.VideoData

sealed interface FoldersStateSealedInter {
    data object LoadingDataOb : FoldersStateSealedInter
    data class Success(val data: List<FolderData>) : FoldersStateSealedInter
}

sealed interface VideosStateSealedInter {
    data object LoadingDataObj : VideosStateSealedInter
    data class SuccessDataClass(val data: List<VideoData>) : VideosStateSealedInter
}
