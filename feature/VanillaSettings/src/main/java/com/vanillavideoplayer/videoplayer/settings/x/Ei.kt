package com.vanillavideoplayer.videoplayer.settings.x

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vanillavideoplayer.videoplayer.core.model.DecoderPriority
import com.vanillavideoplayer.videoplayer.core.model.DoubleTapGestureEnum
import com.vanillavideoplayer.videoplayer.core.model.FastSeek
import com.vanillavideoplayer.videoplayer.core.model.FontEnum
import com.vanillavideoplayer.videoplayer.core.model.ResumeEnum
import com.vanillavideoplayer.videoplayer.core.model.ScreenOrientationEnum
import com.vanillavideoplayer.videoplayer.core.model.Size
import com.vanillavideoplayer.videoplayer.core.model.ThemeConfigEnum
import com.vanillavideoplayer.videoplayer.core.ui.R
import java.util.Locale

@Composable
fun DecoderPriority.nameString(): String {
    val stringRes = when (this) {
        DecoderPriority.PREFER_DEVICE -> R.string.prefer_device_decoders
        DecoderPriority.PREFER_APP -> R.string.prefer_app_decoders
        DecoderPriority.DEVICE_ONLY -> R.string.device_decoders_only
    }

    return stringResource(id = stringRes)
}

@Composable
fun ThemeConfigEnum.nameString(): String {
    val stringRes = when (this) {
        ThemeConfigEnum.THEME_SYSTEM -> R.string.system_default
        ThemeConfigEnum.THEME_OFF -> R.string.off
        ThemeConfigEnum.THEME_ON -> R.string.on
    }

    return stringResource(id = stringRes)
}

fun updateLocale(languageCode: String, context: Context) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val configuration = Configuration(context.resources.configuration)
    configuration.setLocale(locale)
    context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
}

@Composable
fun FastSeek.nameString(): String {
    val stringRes = when (this) {
        FastSeek.AUTO -> R.string.auto
        FastSeek.ENABLE -> R.string.enable
        FastSeek.DISABLE -> R.string.disable
    }

    return stringResource(id = stringRes)
}

@Composable
fun Size.nameString(): String {
    val stringRes = when (this) {
        Size.COMPACT -> R.string.compact
        Size.MEDIUM -> R.string.medium
        Size.LARGE -> R.string.large
    }

    return stringResource(id = stringRes)
}

@Composable
fun DoubleTapGestureEnum.nameString(): String {
    val stringRes = when (this) {
        DoubleTapGestureEnum.DTG_PLAY_PAUSE -> R.string.play_pause
        DoubleTapGestureEnum.DTG_FAST_FORWARD_AND_REWIND -> R.string.ff_rewind
        DoubleTapGestureEnum.DTG_BOTH -> R.string.play_pause_ff_rewind
        DoubleTapGestureEnum.DTG_NONE -> R.string.none
    }

    return stringResource(id = stringRes)
}

@Composable
fun FontEnum.nameString(): String {
    val stringRes = when (this) {
        FontEnum.DEFAULT_FONT -> R.string.default_name
        FontEnum.MONOSPACE_FONT -> R.string.monospace
        FontEnum.SANS_SERIF_FONT -> R.string.sans_serif
        FontEnum.SERIF_FONT -> R.string.serif
    }

    return stringResource(id = stringRes)
}

@Composable
fun ScreenOrientationEnum.nameString(): String {
    val stringRes = when (this) {
        ScreenOrientationEnum.AUTOMATIC_SO -> R.string.automatic
        ScreenOrientationEnum.LANDSCAPE_SO -> R.string.landscape
        ScreenOrientationEnum.LANDSCAPE_REVERSE_SO -> R.string.landscape_reverse
        ScreenOrientationEnum.LANDSCAPE_AUTO_SO -> R.string.landscape_auto
        ScreenOrientationEnum.PORTRAIT_SO -> R.string.portrait
        ScreenOrientationEnum.VIDEO_ORIENTATION_SO -> R.string.video_orientation
    }

    return stringResource(id = stringRes)
}

@Composable
fun ResumeEnum.nameString(): String {
    val stringRes = when (this) {
        ResumeEnum.RESUME_YES -> R.string.yes
        ResumeEnum.RESUME_NO -> R.string.no
    }

    return stringResource(id = stringRes)
}

