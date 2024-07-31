package com.vanillavideoplayer.hd.videoplayer.pro.feature.videopicker.screens

import com.vanillavideoplayer.hd.videoplayer.pro.core.model.FolderData
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.VideoData

sealed interface FoldersStateSealedInter {
    data object LoadingDataOb : FoldersStateSealedInter
    data class Success(val data: List<FolderData>) : FoldersStateSealedInter
}

sealed interface VideosStateSealedInter {
    data object LoadingDataObj : VideosStateSealedInter
    data class SuccessDataClass(val data: List<VideoData>) : VideosStateSealedInter
}
