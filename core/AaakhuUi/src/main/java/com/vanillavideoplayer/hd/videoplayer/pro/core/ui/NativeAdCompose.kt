package com.vanillavideoplayer.hd.videoplayer.pro.core.ui

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.harshil258.adplacer.adViews.NativeMediumOrSmallView
import com.harshil258.adplacer.interfaces.AdCallback
import com.harshil258.adplacer.utils.Constants.runningActivity
import com.harshil258.adplacer.utils.Logger
import com.harshil258.adplacer.utils.STATUS
import com.harshil258.adplacer.utils.SharedPrefConfig.Companion.sharedPrefConfig


@Composable
fun NativeAdCompose(isFromRecyclerview: Boolean? = false, fromWhichScreen: String? = ""): NativeMediumOrSmallView? {
    var myAndroidView: NativeMediumOrSmallView? = null

    val appDetails = sharedPrefConfig.appDetails
    Logger.e("NATIVEADNATIVEAD", "NATIVEAD: ${isFromRecyclerview}  adstatus   ${appDetails.adStatus}")

    if (appDetails.adStatus == STATUS.ON.name) {
        Logger.e("NATIVEADNATIVEAD", "NATIVEAD: 111111")


        AndroidView(factory = { context ->
            Logger.w("NATIVEADNATIVEAD", "AndroidView  NATIVEAD: LOADING")

            myAndroidView = NativeMediumOrSmallView(context)
            myAndroidView!!.loadAd(runningActivity, object : AdCallback {
                override fun adDisplayedCallback(displayed: Boolean) {
                    if (displayed) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            myAndroidView?.rootView?.requestLayout()
                        }, 1000)
                    } else {

                    }
                }
            })
            myAndroidView!!
        })
    }

    return myAndroidView
}

