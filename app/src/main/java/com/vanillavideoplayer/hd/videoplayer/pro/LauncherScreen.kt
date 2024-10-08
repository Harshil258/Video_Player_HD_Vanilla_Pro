package com.vanillavideoplayer.hd.videoplayer.pro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.vanillavideoplayer.hd.videoplayer.pro.settings.x.updateLocale


class LauncherScreen : AppCompatActivity() {

    companion object {
        lateinit var launcherInstance: LauncherScreen
        var instance: LauncherScreen? = null
    }

    val context by lazy { this }

    override fun onCreate(savedInstanceState: Bundle?) {
        instance = this
        launcherInstance = this
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        window.statusBarColor =
            ContextCompat.getColor(this, R.color.white)
        updateLocale(GlobalPreferences().getLangCode(context, ""), context)
    }
}