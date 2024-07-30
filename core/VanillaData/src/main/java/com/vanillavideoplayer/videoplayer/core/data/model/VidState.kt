package com.vanillavideoplayer.videoplayer.core.data.model

import android.net.Uri

data class VidState(
    val path: String,
    val position: Long,
    val audioTrackIndex: Int?,
    val subtitleTrackIndex: Int?,
    val playbackSpeed: Float?,
    val externalSubs: List<Uri>,
    val lastPlayed: Long?,
)
