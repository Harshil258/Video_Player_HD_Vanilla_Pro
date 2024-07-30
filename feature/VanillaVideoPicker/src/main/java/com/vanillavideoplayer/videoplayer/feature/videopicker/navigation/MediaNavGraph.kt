package com.vanillavideoplayer.videoplayer.feature.videopicker.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.harshil258.adplacer.interfaces.InterAdCallBack
import com.harshil258.adplacer.utils.Constants.adPlacerApplication
import com.harshil258.adplacer.utils.Constants.runningActivity
import com.vanillavideoplayer.videoplayer.feature.player.VanillaPlayerActivityPro

const val MEDIA_ROUTE_VAL = "media_nav_route"


fun Context.startVPActivity(uri: Uri) {


    runningActivity?.let {
        adPlacerApplication.interstitialManager.loadAndShowInter(it,object : InterAdCallBack {
        override fun onContinueFlow() {
            val intent = Intent(
                Intent.ACTION_VIEW,
                uri,
                runningActivity,
                VanillaPlayerActivityPro::class.java
            )
            startActivity(intent)
        }
    })
    }
}



