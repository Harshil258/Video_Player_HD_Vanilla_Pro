package com.vanillavideoplayer.videoplayer.feature.player.z

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.vanillavideoplayer.videoplayer.core.common.CommonAi
import com.vanillavideoplayer.videoplayer.core.model.DoubleTapGestureEnum
import com.vanillavideoplayer.videoplayer.core.model.PlayerPref
import com.vanillavideoplayer.videoplayer.feature.player.PlayerViewModel
import com.vanillavideoplayer.videoplayer.feature.player.R
import com.vanillavideoplayer.videoplayer.feature.player.VanillaPlayerActivityPro
import com.vanillavideoplayer.videoplayer.feature.player.x.checkIfFastSeekEnabled
import com.vanillavideoplayer.videoplayer.feature.player.x.seekBackward
import com.vanillavideoplayer.videoplayer.feature.player.x.seekTorward
import com.vanillavideoplayer.videoplayer.feature.player.x.switchPlayPause
import kotlin.math.abs
import kotlin.math.roundToInt
import com.vanillavideoplayer.videoplayer.core.ui.R as coreUiR

enum class GestureEventEnum {
    SWIPE_GASTURE, SEEK_GASTURE, ZOOM_GASTURE, FAST_PLAYBACK_GASTURE
}

inline val Int.convetToMillis get() = this * 1000

fun Resources.convertPxToDp(px: Int) = (px * displayMetrics.density).toInt()

@UnstableApi
@SuppressLint("ClickableViewAccessibility")
class PlayerGestureHandler(
    private val playerViewModel: PlayerViewModel, private val vanillaAct: VanillaPlayerActivityPro, private val vanillaAm: VanillaAudioManager, private val lightManager: LightManager
) {
    private val playerPref: PlayerPref
        get() = playerViewModel.playerPrefs.value

    private val playerView: PlayerView
        get() = vanillaAct.vanillaBinding.playerView

    private val boolFastSeek: Boolean
        get() = playerView.player?.duration?.let { playerPref.checkIfFastSeekEnabled(it) } == true

    private var aspectFrameLayout: AspectRatioFrameLayout = playerView.findViewById(R.id.exo_content_frame)

    private var defaultGestureEventEnum: GestureEventEnum? = null
    private var seekStarting = 0L
    private var post = 0L
    private var seekUpdate = 0L
    private var pointerInt = 1
    private var boolPlayingOnStarting: Boolean = false
    private var presentPlaybackSpeed: Float? = null

    private val tapGD = GestureDetector(playerView.context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
            with(playerView) {
                if (!isControllerFullyVisible) showController() else hideController()
            }
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            if (!playerPref.boolLongPressControls) return
            if (playerView.player?.isPlaying == false) return
            if (defaultGestureEventEnum == null) {
                defaultGestureEventEnum = GestureEventEnum.FAST_PLAYBACK_GASTURE
                presentPlaybackSpeed = playerView.player?.playbackParameters?.speed
            }
            if (defaultGestureEventEnum != GestureEventEnum.FAST_PLAYBACK_GASTURE) return
            if (pointerInt >= 3) return

            playerView.hideController()
            vanillaAct.showTopInfo(vanillaAct.getString(coreUiR.string.fast_playback_speed, playerPref.longPressControlsSpeedVal))
            playerView.player?.setPlaybackSpeed(playerPref.longPressControlsSpeedVal)
        }

        override fun onDoubleTap(event: MotionEvent): Boolean {
            if (vanillaAct.boolControlsLocked) return false

            playerView.player?.run {
                when (playerPref.doubleTapGestureEnum) {
                    DoubleTapGestureEnum.DTG_FAST_FORWARD_AND_REWIND -> {
                        val viewCenterX = playerView.measuredWidth / 2

                        if (event.x.toInt() < viewCenterX) {
                            val newPosition = currentPosition - playerPref.seekIncrementVal.convetToMillis
                            seekBackward(newPosition.coerceAtLeast(0), boolFastSeek)
                        } else {
                            val newPosition = currentPosition + playerPref.seekIncrementVal.convetToMillis
                            seekTorward(newPosition.coerceAtMost(duration), boolFastSeek)
                        }
                    }

                    DoubleTapGestureEnum.DTG_BOTH -> {
                        val eventPositionX = event.x / playerView.measuredWidth

                        if (eventPositionX < 0.35) {
                            val newPosition = currentPosition - playerPref.seekIncrementVal.convetToMillis
                            seekBackward(newPosition.coerceAtLeast(0), boolFastSeek)
                        } else if (eventPositionX > 0.65) {
                            val newPosition = currentPosition + playerPref.seekIncrementVal.convetToMillis
                            seekTorward(newPosition.coerceAtMost(duration), boolFastSeek)
                        } else {
                            playerView.switchPlayPause()
                        }
                    }

                    DoubleTapGestureEnum.DTG_PLAY_PAUSE -> playerView.switchPlayPause()

                    DoubleTapGestureEnum.DTG_NONE -> return false
                }
            } ?: return false
            return true
        }
    })

    private val seekGASTUREGD = GestureDetector(playerView.context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            pelliEvent: MotionEvent?, atyarniEvent: MotionEvent, dX: Float, dY: Float
        ): Boolean {
            if (pelliEvent == null) return false
            if (returnInExclusionPart(pelliEvent)) return false
            if (!playerPref.boolSeekControls) return false
            if (vanillaAct.boolControlsLocked) return false
            if (!vanillaAct.isFileLoaded) return false
            if (abs(dX / dY) < 2) return false

            if (defaultGestureEventEnum == null) {
                seekUpdate = 0L
                seekStarting = playerView.player?.currentPosition ?: 0L
                playerView.controllerAutoShow = playerView.isControllerFullyVisible
                if (playerView.player?.isPlaying == true) {
                    playerView.player?.pause()
                    boolPlayingOnStarting = true
                }
                defaultGestureEventEnum = GestureEventEnum.SEEK_GASTURE
            }
            if (defaultGestureEventEnum != GestureEventEnum.SEEK_GASTURE) return false

            val distanceDiff = abs(CommonAi.convertPxToDp(dX) / 4).coerceIn(0.5f, 10f)
            val change = (distanceDiff * SEEK_STEP_MS).toLong()

            playerView.player?.run {
                if (dX < 0L) {
                    seekUpdate = (seekUpdate + change).takeIf { it + seekStarting < duration } ?: (duration - seekStarting)
                    post = (seekStarting + seekUpdate).coerceAtMost(duration)
                    seekTorward(pos = post, isFSeek = boolFastSeek)
                } else {
                    seekUpdate = (seekUpdate - change).takeIf { it + seekStarting > 0 } ?: (0 - seekStarting)
                    post = seekStarting + seekUpdate
                    seekBackward(pos = post, isFSeek = boolFastSeek)
                }
                vanillaAct.showPlayerInfo(
                    info = CommonAi.formatDurationInHours(this.currentPosition), subInfo = "[${CommonAi.formatDurationSign(seekUpdate)}]"
                )
                return true
            }
            return false
        }
    })

    private val audioAndLightGD = GestureDetector(playerView.context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            pelliEvent: MotionEvent?, presentEvent: MotionEvent, dX: Float, dY: Float
        ): Boolean {
            if (pelliEvent == null) return false
            if (returnInExclusionPart(pelliEvent)) return false
            if (!playerPref.boolSwipeControls) return false
            if (vanillaAct.boolControlsLocked) return false
            if (abs(dY / dX) < 2) return false

            if (defaultGestureEventEnum == null) {
                defaultGestureEventEnum = GestureEventEnum.SWIPE_GASTURE
            }
            if (defaultGestureEventEnum != GestureEventEnum.SWIPE_GASTURE) return false

            val viewCenterX = playerView.measuredWidth / 2
            val distanceFull = playerView.measuredHeight * FULL_SWIPE_RANGE_SCREEN_RATIO
            val ratioChange = dY / distanceFull

            if (pelliEvent.x.toInt() > viewCenterX) {
                val change = ratioChange * vanillaAm.maxStreamLevel
                vanillaAm.updateVolume(vanillaAm.defaultVolume + change, playerPref.systemVolumePanelDekhadu)
                vanillaAct.showVolumeGestureLayout()
            } else {
                val change = ratioChange * lightManager.maxLightLevel
                lightManager.updateLight(lightManager.presentLightLevel + change)
                vanillaAct.showBrightnessGestureLayout()
            }
            return true
        }
    })

    private val zoomGASTUREGD = ScaleGestureDetector(playerView.context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        private val SCALE_RANGE = 0.25f..4.0f

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            if (!playerPref.boolZoomControls || vanillaAct.boolControlsLocked) {
                return false
            }

            if (playerView.parent !is ViewGroup) return false
            val videoSize = vanillaAct.currentVideoSize ?: return false

            val scaleFact = (aspectFrameLayout.scaleX * detector.scaleFactor)
            val changedVidScale = (aspectFrameLayout.width * scaleFact) / videoSize.width.toFloat()

            if (changedVidScale in SCALE_RANGE) {
                aspectFrameLayout.scaleX = scaleFact
                aspectFrameLayout.scaleY = scaleFact
                try {
                    val presentVideoScale = (aspectFrameLayout.width * aspectFrameLayout.scaleX) / videoSize.width.toFloat() * 100
                    vanillaAct.showPlayerInfo("${if (!presentVideoScale.isNaN()) presentVideoScale.roundToInt() else 0}%")
                } catch (_: Exception) {
                }
            }

            return true
        }
    })


    private fun freeUpGestures() {
        vanillaAct.hideVolumeGestureLayout()
        vanillaAct.hideBrightnessGestureLayout()
        vanillaAct.hidePlayerInfo(0L)
        vanillaAct.hideTopInfo()

        presentPlaybackSpeed?.let {
            playerView.player?.setPlaybackSpeed(it)
            presentPlaybackSpeed = null
        }

        playerView.controllerAutoShow = true
        if (boolPlayingOnStarting) playerView.player?.play()
        boolPlayingOnStarting = false
        defaultGestureEventEnum = null
    }

    private fun returnInExclusionPart(firstEvent: MotionEvent): Boolean {
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val insets = playerView.rootWindowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemGestures())

            if ((firstEvent.x < insets.left) || (firstEvent.x > (screenWidth - insets.right)) || (firstEvent.y < insets.top) || (firstEvent.y > (screenHeight - insets.bottom))) {
                return true
            }
        } else if (firstEvent.y < playerView.resources.convertPxToDp(GESTURE_EXCLUSION_AREA_VERTICAL) || firstEvent.y > screenHeight - playerView.resources.convertPxToDp(GESTURE_EXCLUSION_AREA_VERTICAL) || firstEvent.x < playerView.resources.convertPxToDp(GESTURE_EXCLUSION_AREA_HORIZONTAL) || firstEvent.x > screenWidth - playerView.resources.convertPxToDp(GESTURE_EXCLUSION_AREA_HORIZONTAL)) {
            return true
        }
        return false
    }

    init {
        playerView.setOnTouchListener { _, motionEvent ->
            pointerInt = motionEvent.pointerCount
            when (motionEvent.pointerCount) {
                1 -> {
                    tapGD.onTouchEvent(motionEvent)
                    audioAndLightGD.onTouchEvent(motionEvent)
                    seekGASTUREGD.onTouchEvent(motionEvent)
                }

                2 -> {
                    zoomGASTUREGD.onTouchEvent(motionEvent)
                }
            }

            if (motionEvent.action == MotionEvent.ACTION_UP || motionEvent.pointerCount >= 3) {
                freeUpGestures()
            }
            true
        }
    }

    companion object {
        const val FULL_SWIPE_RANGE_SCREEN_RATIO = 0.66f
        const val GESTURE_EXCLUSION_AREA_VERTICAL = 48
        const val GESTURE_EXCLUSION_AREA_HORIZONTAL = 24
        const val SEEK_STEP_MS = 1000L
    }
}

