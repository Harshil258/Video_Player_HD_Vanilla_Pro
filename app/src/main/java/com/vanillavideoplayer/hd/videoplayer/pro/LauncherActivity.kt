package com.vanillavideoplayer.hd.videoplayer.pro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.vanillavideoplayer.videoplayer.R
import com.vanillavideoplayer.videoplayer.settings.x.updateLocale


class LauncherActivity : AppCompatActivity() {

    companion object {
        lateinit var launcherInstance: LauncherActivity
        var instance: LauncherActivity? = null
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