package com.vanillavideoplayer.videoplayer.core.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayerPref(
    val resumeEnum: ResumeEnum = ResumeEnum.RESUME_YES,
    val savePlayerBright: Boolean = true,
    val playerBright: Float = 0.5f,
    val fastSeek: FastSeek = FastSeek.AUTO,
    val minLongForFastSeek: Long = 120000L,
    val saveSelections: Boolean = true,
    val playerScreenOrientationEnum: ScreenOrientationEnum = ScreenOrientationEnum.AUTOMATIC_SO,
    val playerVidZoomEnum: VidZoomEnum = VidZoomEnum.VANILLA_FIT,
    val defaultPlaySpeed: Float = 1.0f,
    val optionsAutoHideTimeout: Int = 2,
    val seekIncrementVal: Int = 10,
    val boolAutoplay: Boolean = true,

    val boolSwipeControls: Boolean = true,
    val boolSeekControls: Boolean = true,
    val boolZoomControls: Boolean = true,
    val doubleTapGestureEnum: DoubleTapGestureEnum = DoubleTapGestureEnum.DTG_BOTH,
    val boolLongPressControls: Boolean = true,
    val longPressControlsSpeedVal: Float = 2.0f,

    val suggestedAudioLang: String = "",
    val boolPauseOnHeadsetDisconnect: Boolean = true,
    val audioFocusNiJarurChheKeNai: Boolean = true,
    val systemVolumePanelDekhadu: Boolean = true,

    val boolSystemCaptionStyle: Boolean = false,
    val suggestedSubtitleLang: String = "",
    val subtitleTextEncodingVal: String = "",
    val subtitleTextSizeVal: Int = 23,
    val boolSubtitleBackground: Boolean = false,
    val subtitleFontEnum: FontEnum = FontEnum.DEFAULT_FONT,
    val boolSubtitleTextBold: Boolean = true,
    val boolApplyEmbeddedStyles: Boolean = true,

    val decoderPriority: DecoderPriority = DecoderPriority.PREFER_DEVICE
)
