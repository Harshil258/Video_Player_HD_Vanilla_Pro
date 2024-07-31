package com.vanillavideoplayer.hd.videoplayer.pro.core.common

import android.content.res.Resources
import java.util.concurrent.TimeUnit
import kotlin.math.abs

object CommonAi {

    fun returnFormattedFileSize(size: Long): String {
        val kb = 1024
        val mb = kb * 1024
        val gb = mb * 1024

        return when {
            size < kb -> "$size B"
            size < mb -> "%.2f KB".format(size / kb.toDouble())
            size < gb -> "%.2f MB".format(size / mb.toDouble())
            else -> "%.2f GB".format(size / gb.toDouble())
        }
    }

    fun convertPxToDp(px: Float): Float {
        return px / Resources.getSystem().displayMetrics.density
    }

    fun formatDurationSign(millis: Long): String {
        return if (millis >= 0) {
            "+${formatDurationInHours(millis)}"
        } else {
            "-${formatDurationInHours(abs(millis))}"
        }
    }

    fun formatDurationInHours(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hours)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(minutes) -
                TimeUnit.HOURS.toSeconds(hours)
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

}
