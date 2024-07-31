package com.vanillavideoplayer.hd.videoplayer.pro.feature.player.z

import android.net.Uri

class PlaylistHandler {

    private var mutableQueueList = mutableListOf<Uri>()

    override fun toString(): String = buildString {
        try {
            append("########## playlist ##########\n")
            val copiedList = ArrayList(mutableQueueList)
            copiedList.filterNotNull().forEach { append(it.toString() + "\n") }
            append("##############################")
        } catch (_: Exception) {
        }
    }

    fun returnQueueListSize() = mutableQueueList.size

    fun changeQueueList(items: List<Uri>) {
        if (items == mutableQueueList) {
            return
        }
        try {
            mutableQueueList.clear()
            mutableQueueList.addAll(items)
        } catch (_: Exception) {
            try {
                mutableQueueList = items.toMutableList()
            } catch (_: Exception) {
            }
        }
    }

    fun clearQueue() {
        try {
            mutableQueueList.clear()
        } catch (_: Exception) {
            try {
                mutableQueueList = mutableListOf()
            } catch (_: Exception) {
            }
        }
        defaultItem = null
    }


    private var defaultItem: Uri? = null

    fun getPointedUri(): Uri? = defaultItem

    fun updatePointedItemUri(uri: Uri) {
        defaultItem = uri
    }

    private fun pointingIndex(): Int = mutableQueueList.indexOfFirst { it == defaultItem }.takeIf { it >= 0 } ?: 0

    fun isNextPossible(): Boolean {
        return pointingIndex() + 1 < returnQueueListSize()
    }

    fun returnNextUri(): Uri? = mutableQueueList.getOrNull(pointingIndex() + 1)

    fun isPrevPossible(): Boolean {
        return pointingIndex() > 0
    }

    fun returnPrevUri(): Uri? = mutableQueueList.getOrNull(pointingIndex() - 1)

}
