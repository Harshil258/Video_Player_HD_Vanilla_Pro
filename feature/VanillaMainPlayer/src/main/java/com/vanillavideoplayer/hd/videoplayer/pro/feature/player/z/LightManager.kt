package com.vanillavideoplayer.hd.videoplayer.pro.feature.player.z

import android.provider.Settings
import android.view.WindowManager
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.VanillaPlayerActivityPro
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.x.currentBrightness
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.x.swipeToShowStatusBars

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
