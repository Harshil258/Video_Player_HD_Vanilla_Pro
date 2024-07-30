package com.vanillavideoplayer.videoplayer.core.model

import java.io.Serializable

data class VideoData(
    val id: Long,
    val path: String,
    val parentPath: String = "",
    val duration: Long,
    val uriString: String,
    val displayName: String,
    val nameWithExtension: String,
    val width: Int,
    val height: Int,
    val size: Long,
    var isSelected: Boolean = false,
    val dateModified: Long = 0,
    val formattedDuration: String = "",
    val formattedFileSize: String = "",
    val lastPlayed: Long?
) : Serializable {

    companion object {
        val sample = VideoData(
            id = 5,
            path = "/storage/emulated/0/Download/Most Eligible Bachelor (2019).mp4",
            parentPath = "/storage/emulated/0/Movie",
            uriString = "",
            nameWithExtension = "Most Eligible Bachelor (2019).mp4",
            duration = 1000,
            displayName = "Most Eligible Bachelor (2019)",
            width = 1920,
            height = 1080,
            isSelected = false,
            size = 1000,
            formattedDuration = "22.56",
            formattedFileSize = "320KB",
            lastPlayed = 0
        )
    }
}
