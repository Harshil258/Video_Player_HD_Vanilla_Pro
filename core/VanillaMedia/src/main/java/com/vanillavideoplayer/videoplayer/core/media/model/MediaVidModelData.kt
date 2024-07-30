package com.vanillavideoplayer.videoplayer.core.media.model

import android.net.Uri

data class MediaVidModelData(
    val id: Long,
    val uri: Uri,
    val size: Long,
    val width: Int,
    val height: Int,
    val data: String,
    val duration: Long,
    val dateModified: Long
)
