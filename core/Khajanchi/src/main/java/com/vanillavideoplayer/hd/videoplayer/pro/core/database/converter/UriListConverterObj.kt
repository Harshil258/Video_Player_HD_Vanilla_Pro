package com.vanillavideoplayer.hd.videoplayer.pro.core.database.converter

import android.net.Uri
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object UriListConverterObj {
    fun proceedFromStringToList(urlString: String): List<Uri> {
        if (urlString.isEmpty()) return emptyList()
        return urlString.split(",").map {
            val subString = URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
            Uri.parse(subString)
        }
    }


    fun proceedFromListToString(urlList: List<Uri>): String {
        if (urlList.isEmpty()) return ""
        return urlList.map { URLEncoder.encode(it.toString(), StandardCharsets.UTF_8.toString()) }
            .reduce { acc, s -> "$acc,$s" }
    }

}
