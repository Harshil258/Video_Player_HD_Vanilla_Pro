package com.vanillavideoplayer.hd.videoplayer.pro.core.ui

import android.net.Uri

object PlaylistKeeper {

    var playList: ArrayList<Uri> = ArrayList()

    fun setPlaylistUris(list: ArrayList<Uri>) {
        playList = list
    }

    fun getPlaylistFromUri(): ArrayList<Uri> {
        return playList
    }
}