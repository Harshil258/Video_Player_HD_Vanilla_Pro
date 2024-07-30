package com.vanillavideoplayer.videoplayer.core.model

data class FolderData(
    val name: String,
    val path: String,
    val mediaSize: Long,
    val mediaCount: Int,
    val dateModified: Long,
    val formattedMediaSize: String = ""
) {

    companion object {
        val sample = FolderData(
            name = "New Folder",
            path = "/storage/emulated/0/DCIM/Camera/Custom Photos",
            mediaSize = 1024,
            mediaCount = 1,
            dateModified = 200,
            formattedMediaSize = "3KB"
        )
    }
}
