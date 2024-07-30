package com.vanillavideoplayer.videoplayer.feature.player.z

import android.media.AudioManager
import android.media.audiofx.LoudnessEnhancer

class VanillaAudioManager(private val am: AudioManager) {

    var loudnessEnhancer: LoudnessEnhancer? = null
        set(value) {
            if (defaultVolume > maxStreamLevel) {
                try {
                    value?.enabled = true
                    value?.setTargetGain(defaultLoudnessGain.toInt())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            field = value
        }
    val defaultStreamVolume get() = am.getStreamVolume(AudioManager.STREAM_MUSIC)
    val maxStreamLevel get() = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    var defaultVolume = defaultStreamVolume.toFloat()
        private set
    val maxVolumeLevel get() = maxStreamLevel.times(loudnessEnhancer?.let { 2 } ?: 1)

    val defaultLoudnessGain get() = (defaultVolume - maxStreamLevel) * (MAX_AUDIO_BOOST_LEVEL / maxStreamLevel)
    val audioLevelPercentage get() = (defaultVolume / maxStreamLevel.toFloat()).times(100).toInt()

    @Suppress("DEPRECATION")
    fun updateVolume(volume: Float, showVolumePanel: Boolean = false) {
        defaultVolume = volume.coerceIn(0f, maxVolumeLevel.toFloat())

        if (defaultVolume <= maxStreamLevel) {
            loudnessEnhancer?.enabled = false
            am.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                defaultVolume.toInt(),
                if (showVolumePanel && am.isWiredHeadsetOn) AudioManager.FLAG_SHOW_UI else 0
            )
        } else {
            try {
                loudnessEnhancer?.enabled = true
                loudnessEnhancer?.setTargetGain(defaultLoudnessGain.toInt())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun audioVolumeUP(volPanel: Boolean = false) {
        updateVolume(defaultVolume + 1, volPanel)
    }

    fun audioVolumeDown(boolVolumePanel: Boolean = false) {
        updateVolume(defaultVolume - 1, boolVolumePanel)
    }

    companion object {
        const val MAX_AUDIO_BOOST_LEVEL = 2000
    }
}
