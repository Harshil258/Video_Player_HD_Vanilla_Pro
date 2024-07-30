package com.vanillavideoplayer.videoplayer.feature.player.z

import android.provider.Settings
import android.view.WindowManager
import com.vanillavideoplayer.videoplayer.feature.player.VanillaPlayerActivityPro
import com.vanillavideoplayer.videoplayer.feature.player.x.currentBrightness
import com.vanillavideoplayer.videoplayer.feature.player.x.swipeToShowStatusBars

class LightManager(private val activity: VanillaPlayerActivityPro) {

    var presentLightLevel: Float = 0f

    init {
        try {
            presentLightLevel = activity.currentBrightness
        } catch (_: Settings.SettingNotFoundException) {
        }
    }

    val maxLightLevel = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL

    val lightPercentage get() = (presentLightLevel / maxLightLevel).times(100).toInt()

    fun updateLight(float: Float) {
        presentLightLevel = float.coerceIn(0f, maxLightLevel)
        val layoutParams = activity.window.attributes
        layoutParams.screenBrightness = presentLightLevel
        activity.window.attributes = layoutParams

        activity.swipeToShowStatusBars()
    }
}
