package com.vanillavideoplayer.hd.videoplayer.pro.feature.player.x

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.view.WindowManager
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.C
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.TrackGroup
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.exoplayer.trackselection.MappingTrackSelector
import androidx.media3.ui.PlayerView
import com.vanillavideoplayer.hd.videoplayer.pro.core.common.extra.getFNameFromUri
import com.vanillavideoplayer.hd.videoplayer.pro.core.common.extra.getFileSub
import com.vanillavideoplayer.hd.videoplayer.pro.core.common.extra.getPathFromUri
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.FastSeek
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.FontEnum
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.PlayerPref
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.ScreenOrientationEnum
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.model.SubDataModel
import timber.log.Timber
import java.io.File
import java.util.Arrays
import java.util.Locale

fun Activity.swipeToShowStatusBars() {
    WindowCompat.getInsetsController(window, window.decorView).systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
}

inline fun <reified T : Enum<T>> T.next(): T {
    val values = enumValues<T>()
    val nextOrdinal = (ordinal + 1) % values.size
    return values[nextOrdinal]
}

fun Activity.toggleSystemBars(
    showBars: Boolean,
    @WindowInsetsCompat.Type.InsetsType types: Int = WindowInsetsCompat.Type.systemBars()
) {
    WindowCompat.getInsetsController(window, window.decorView).apply {
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        if (showBars) show(types) else hide(types)
    }
}

fun FontEnum.convertToTf(): Typeface = when (this) {
    FontEnum.DEFAULT_FONT -> Typeface.DEFAULT
    FontEnum.MONOSPACE_FONT -> Typeface.MONOSPACE
    FontEnum.SANS_SERIF_FONT -> Typeface.SANS_SERIF
    FontEnum.SERIF_FONT -> Typeface.SERIF
}

val Activity.currentBrightness: Float
    get() = when (val brightness = window.attributes.screenBrightness) {
        in WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF..WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL -> brightness
        else -> Settings.System.getFloat(contentResolver, Settings.System.SCREEN_BRIGHTNESS) / 255
    }

fun ImageButton.attachImgDrawable(context: Context, id: Int) {
    setImageDrawable(ContextCompat.getDrawable(context, id))
}

@Suppress("DEPRECATION")
fun Activity.prettyPrintIntent() {
    try {
        intent?.let {
            Timber.apply {
                d("* action: ${it.action}")
                d("* data: ${it.data}")
                d("* type: ${it.type}")
                d("* package: ${it.`package`}")
                d("* component: ${it.component}")
                d("* flags: ${it.flags}")
                it.extras?.let { bundle ->
                    d("=== Extras ===")
                    bundle.keySet().forEachIndexed { i, key ->
                        buildString {
                            append("${i + 1}) $key: ")
                            bundle.get(key)
                                .let { append(if (it is Array<*>) Arrays.toString(it) else it) }
                        }.also { d(it) }
                    }
                }
            }
        }
    } catch (_: Exception) {
    }
}

@UnstableApi
fun MappingTrackSelector.MappedTrackInfo.checkIfRendererFree(
    type: @C.TrackType Int
): Boolean {
    for (i in 0 until rendererCount) {
        if (getTrackGroups(i).length == 0) continue
        if (type == getRendererType(i)) return true
    }
    return false
}


fun Uri.convertInSub(context: Context) = SubDataModel(
    subName = context.getFNameFromUri(this),
    subUri = this,
    boolSelected = false
)

fun Uri.returnSubMime(): String {
    return when {
        path?.endsWith(".ssa") == true || path?.endsWith(".ass") == true -> {
            MimeTypes.TEXT_SSA
        }

        path?.endsWith(".vtt") == true -> {
            MimeTypes.TEXT_VTT
        }

        path?.endsWith(".ttml") == true || path?.endsWith(".xml") == true || path?.endsWith(".dfxp") == true -> {
            MimeTypes.APPLICATION_TTML
        }

        else -> {
            MimeTypes.APPLICATION_SUBRIP
        }
    }
}

@Suppress("DEPRECATION")
fun Bundle.returnParcelableUriArray(key: String): Array<out Parcelable>? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableArray(key, Uri::class.java)
    } else {
        getParcelableArray(key)
    }
}

val VideoSize.checkIfPortrait: Boolean
    get() {
        val boolRotated =
            this.unappliedRotationDegrees == 90 || this.unappliedRotationDegrees == 270
        return if (boolRotated) this.width > this.height else this.height > this.width
    }


fun Uri.returnDesiSubs(
    context: Context,
    excludeSubsList: List<Uri> = emptyList()
): List<SubDataModel> {
    return context.getPathFromUri(this)?.let { path ->
        val excludeSubsPathList = excludeSubsList.mapNotNull { context.getPathFromUri(it) }
        File(path).getFileSub().mapNotNull { file ->
            if (file.path !in excludeSubsPathList) {
                SubDataModel(
                    subName = file.name,
                    subUri = file.toUri(),
                    boolSelected = false
                )
            } else {
                null
            }
        }
    } ?: emptyList()
}


fun Player.toggleTrack(type: @C.TrackType Int, i: Int?) {
    if (i == null) return
    val trackTypeText = when (type) {
        C.TRACK_TYPE_AUDIO -> "audio"
        C.TRACK_TYPE_TEXT -> "subtitle"
        else -> throw IllegalArgumentException("Invalid track type: $type")
    }

    if (i < 0) {
        Timber.d("Disabling $trackTypeText")
        trackSelectionParameters = trackSelectionParameters
            .buildUpon()
            .setTrackTypeDisabled(type, true)
            .build()
    } else {
        val tracks = currentTracks.groups.filter { it.type == type }

        if (tracks.isEmpty() || i >= tracks.size) {
            Timber.d("Operation failed: Invalid track index: $i")
            return
        }

        Timber.d("Setting $trackTypeText track: $i")
        val trackSelectionOverride = TrackSelectionOverride(tracks[i].mediaTrackGroup, 0)

        // Override the track selection parameters to force the selection of the specified track.
        trackSelectionParameters = trackSelectionParameters
            .buildUpon()
            .setTrackTypeDisabled(type, false)
            .setOverrideForType(trackSelectionOverride)
            .build()
    }
}

fun PlayerPref.checkIfFastSeekEnabled(long: Long): Boolean {
    return when (fastSeek) {
        FastSeek.ENABLE -> true
        FastSeek.DISABLE -> false
        FastSeek.AUTO -> long >= minLongForFastSeek
    }
}

@UnstableApi
fun Player.changeSeekParameters(seekParams: SeekParameters) {
    when (this) {
        is ExoPlayer -> this.setSeekParameters(seekParams)
    }
}

@UnstableApi
fun PlayerView.switchPlayPause() {
    this.controllerAutoShow = this.isControllerFullyVisible
    if (this.player?.isPlaying == true) {
        this.player?.pause()
    } else {
        this.player?.play()
    }
}

@UnstableApi
fun Player.seekBackward(pos: Long, isFSeek: Boolean = false) {
    changeSeekParameters(if (isFSeek) SeekParameters.PREVIOUS_SYNC else SeekParameters.DEFAULT)
    this.seekTo(pos)
}

fun ScreenOrientationEnum.returnActOrient(videoOrientation: Int? = null): Int {
    return when (this) {
        ScreenOrientationEnum.AUTOMATIC_SO -> ActivityInfo.SCREEN_ORIENTATION_SENSOR
        ScreenOrientationEnum.LANDSCAPE_SO -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        ScreenOrientationEnum.LANDSCAPE_REVERSE_SO -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        ScreenOrientationEnum.LANDSCAPE_AUTO_SO -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        ScreenOrientationEnum.PORTRAIT_SO -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        ScreenOrientationEnum.VIDEO_ORIENTATION_SO ->
            videoOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}

@UnstableApi
fun Player.seekTorward(pos: Long, isFSeek: Boolean = false) {
    changeSeekParameters(if (isFSeek) SeekParameters.NEXT_SYNC else SeekParameters.DEFAULT)
    this.seekTo(pos)
}

@UnstableApi
fun TrackGroup.returnTrackName(type: @C.TrackType Int, i: Int): String {
    val format = this.getFormat(0)
    val language = format.language
    val label = format.label
    return buildString {
        if (label != null) {
            append(label)
        }
        if (isEmpty()) {
            if (type == C.TRACK_TYPE_TEXT) {
                append("Subtitle Track #${i + 1}")
            } else {
                append("Audio Track #${i + 1}")
            }
        }
        if (language != null && language != "und") {
            append(" - ")
            append(Locale(language).displayLanguage)
        }
    }
}

@get:UnstableApi
val Player.getAudioSessionId: Int
    get() = when (this) {
        is ExoPlayer -> this.audioSessionId
        else -> C.AUDIO_SESSION_ID_UNSET
    }

fun Player.returnTrackIndex(type: @C.TrackType Int): Int {
    return currentTracks.groups
        .filter { it.type == type && it.isSupported }
        .indexOfFirst { it.isSelected }
}