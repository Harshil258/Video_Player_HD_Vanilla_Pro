package com.vanillavideoplayer.hd.videoplayer.pro


import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.harshil258.adplacer.R
import com.harshil258.adplacer.adClass.AppOpenManager
import com.harshil258.adplacer.adClass.InterstitialManager
import com.harshil258.adplacer.adViews.BannerView
import com.harshil258.adplacer.adViews.NativeBigView
import com.harshil258.adplacer.app.AdPlacerApplication
import com.harshil258.adplacer.interfaces.AdCallback
import com.harshil258.adplacer.interfaces.DialogCallBack
import com.harshil258.adplacer.interfaces.MessagingCallback
import com.harshil258.adplacer.models.ADTYPE
import com.harshil258.adplacer.models.TYPE_OF_RESPONSE
import com.harshil258.adplacer.utils.Constants.isAppInForeground
import com.harshil258.adplacer.utils.Constants.isSplashRunning
import com.harshil258.adplacer.utils.Constants.runningActivity
import com.harshil258.adplacer.utils.Constants.shouldGoWithoutInternet
import com.harshil258.adplacer.utils.Constants.showLogs
import com.harshil258.adplacer.utils.Constants.testDeviceIds
import com.harshil258.adplacer.utils.DialogUtil.createSimpleDialog
import com.harshil258.adplacer.utils.GlobalUtils
import com.harshil258.adplacer.utils.GlobalUtils.Companion.checkMultipleClick
import com.harshil258.adplacer.utils.Logger
import com.harshil258.adplacer.utils.Logger.TAG
import com.harshil258.adplacer.utils.STATUS
import com.harshil258.adplacer.utils.SharedPrefConfig.Companion.sharedPrefConfig
import com.vanillavideoplayer.videoplayer.core.common.commonobj.ApplicationScope
import com.vanillavideoplayer.videoplayer.core.data.repo.PrefRepo
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltAndroidApp
class VanillaProApp : Application(), LifecycleObserver, ActivityLifecycleCallbacks, MessagingCallback {

    @Inject
    lateinit var prefRepo: PrefRepo

    @Inject
    @ApplicationScope
    lateinit var appScope: CoroutineScope

    var vanillaPlayerProAdPlacer: AdPlacerApplication? = null


    override fun onCreate() {
        super.onCreate()
        context = applicationContext

        testDeviceIds.add("07273c52-e840-4a96-8996-b34b55560af5")
        testDeviceIds.add("7E80BC9AF292C150B99F811D3F12279D")
        // TODO : Update this variable too false
        showLogs = false
        Logger.i("TAGCOMMON", "Application   onCreate:  FIRST LOG")

        FirebaseApp.initializeApp(this)
        appScope.launch {
            prefRepo.applicationPrefData.first()
            prefRepo.playerPref.first()
        }

        vanillaProApp = this
        vanillaPlayerProAdPlacer = AdPlacerApplication(this)

        shouldGoWithoutInternet = true

        StrictMode.setThreadPolicy(ThreadPolicy.Builder().detectAll().build())

        registerActivityLifecycleCallbacks(this)
        vanillaPlayerProAdPlacer?.processLifecycleRegister(this)
        vanillaPlayerProAdPlacer?.registerMessagingCallback(this)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        if (!isSplashRunning) {
            vanillaPlayerProAdPlacer?.showAppOpenAd()
        } else if (isSplashRunning && vanillaPlayerProAdPlacer?.appOpenManager?.isAdAvailable == true && !AppOpenManager.isAdShowing) {
            vanillaPlayerProAdPlacer?.showAppOpenAd()
        } else {
        }
    }


    override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
        super.onActivityPreCreated(activity, savedInstanceState)
        runningActivity = activity
        if (activity is LauncherScreen) {
            isSplashRunning = true
        }
    }

    private fun isAllPermGranted(context: Context, permArr: Array<String>): Boolean {
        var allgranted = true
        for (it in permArr) {
            if (ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED) {
                allgranted = false
            }
        }
        return allgranted
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        // activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        runningActivity = activity
        if (activity is LauncherScreen) {
            isSplashRunning = true
            if (!GlobalUtils().isNetworkAvailable(applicationContext)) {
                if (shouldGoWithoutInternet) {
                    vanillaPlayerProAdPlacer?.startTimerForContinueFlow(10)
                    return
                }
                vanillaPlayerProAdPlacer?.initClickCounts()
                vanillaPlayerProAdPlacer?.showInternetDialog()
                return
            } else {
                Logger.i("srherhse", "apiResponse called")
                var responseType = TYPE_OF_RESPONSE.GOOGLE
                responseType.value = "APPDETAILS12"
                vanillaPlayerProAdPlacer?.fetchApiResponse(responseType)
//                if(runningActivity is LauncherActivity){
//                    adPlacerApplication?.startTimerForContinueFlow(12000)
//                }
            }
        } else if (activity is MainActivity) {
//            OneSignal.Debug.logLevel = LogLevel.DEBUG
//            //TODO : Remove this line on release
            vanillaPlayerProAdPlacer?.showOneSignalNotificationPrompt()
//            adPlacerApplication?.subscribeToTheTopic("ONESIGNAL_TESTING")
            vanillaPlayerProAdPlacer?.subscribeToTheTopic("MOVIE_REMINDER")
        }
    }

    override fun showNetworkDialog() {
        createSimpleDialog(runningActivity, "No Internet",
            description = "No internet connection!\nCheck your internet connection",
            negativeButtonText = "Exit",
            positiveButtonText = "Retry", object : DialogCallBack {
            override fun onPositiveClicked(dialog: Dialog) {
                if (GlobalUtils().isNetworkAvailable(runningActivity!!.applicationContext)) {
                    dialog.cancel()
                    showSplashLoader()
                    var responseType = TYPE_OF_RESPONSE.GOOGLE
                    responseType.value = "APPDETAILS12"
                    vanillaPlayerProAdPlacer?.fetchApiResponse(responseType)
                    vanillaPlayerProAdPlacer?.startTimerForContinueFlow(6000)
                } else {
                    if (!checkMultipleClick(2000)) {
                        Toast.makeText(
                            runningActivity!!.applicationContext, "Please connect to internet!", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onNegativeClicked(dialog: Dialog) {
                runningActivity!!.finishAffinity()
                dialog.cancel()
            }

            override fun onDialogCancelled() {

            }

            override fun onDialogDismissed() {
            }
        }, isCancelable = true)
    }

    override fun exitTheApplication() {
        runningActivity?.finish()
    }

    override fun onActivityStarted(activity: Activity) {
        runningActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        runningActivity = activity
    }


    override fun onActivityPaused(activity: Activity) {
        isAppInForeground = false
        try {
            if (InterstitialManager.timer != null) {
                InterstitialManager.timer?.pause()
                InterstitialManager.timer?.cancel()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        vanillaPlayerProAdPlacer?.interstitialManager?.stopLoadingdialog()
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }


    override fun hideSplashLoader() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (LauncherScreen.instance != null) {
                try {
//                    LauncherActivity.instance.runOnUiThread(Runnable {
//                        LauncherActivity.instance.findViewById(
//                            com.harshil258.adplacer.R.id.animationLoading
//                        ).setVisibility(View.GONE)
//                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }, 1000)
    }

    override fun showSplashLoader() {
        if (LauncherScreen.instance != null) {
            try {
//                LauncherActivity.instance.runOnUiThread(Runnable {
//                    LauncherActivity.instance.findViewById(
//                        R.id.animationLoading
//                    ).setVisibility(View.VISIBLE)
//                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override val isExitActivity: Boolean
        get() = false


    override fun openStartActivity() {
//        val intent = Intent(runningActivity, StartActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        startActivity(intent)
//        runningActivity!!.finish()
    }

    override fun openHomeActivity() {
        Logger.e("TAGCOMMON", "MainActivity    ${sharedPrefConfig.appDetails.admobNativeAd}")
        val intent = Intent(runningActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        runningActivity!!.finish()
    }

    override fun savingApiResponse() {
        //      TODO : Check this code too
        val isDebugMode = false
        activateTestIds(isDebugMode)
    }

    override fun startingTimerToChangeScreen() {
        Logger.e(TAG, "continueAppFlow    -->  startingTimerToChangeScreen  -->    called")

        sharedPrefConfig.appDetails.adStatus = STATUS.OFF.name

        if (sharedPrefConfig.appDetails.adStatus == STATUS.ON.name) {
            setAdStatusBasedOnPurchaseOrArgument()
        }


//      TODO : Check this code too
//      val isDebugMode = BuildConfig.DEBUG
        val isDebugMode = true
        activateTestIds(isDebugMode)
    }


    private fun activateTestIds(isDebugMode: Boolean) {
        if (isDebugMode) {
//            setAdMobTestIds()
        }
    }


    fun setAdMobTestIds() {
        sharedPrefConfig.appDetails = sharedPrefConfig.appDetails.copy(
//            admobNativeAd = "ca-app-pub-3940256099942544/2247696110",
            admobNativeAd = "/6499/example/native",
            admobAppOpenAd = "ca-app-pub-3940256099942544/9257395921",
            admobBannerAd = "ca-app-pub-3940256099942544/9214589741",
            admobRewardAd = "ca-app-pub-3940256099942544/5224354917",
            admobInterstitialAd = "ca-app-pub-3940256099942544/1033173712"
        )
//        sharedPrefConfig.appDetails.admobNativeAd = "ca-app-pub-3940256099942544/2247696110"
//        sharedPrefConfig.appDetails.admobAppOpenAd = "ca-app-pub-3940256099942544/9257395921"
//        sharedPrefConfig.appDetails.admobBannerAd = "ca-app-pub-3940256099942544/9214589741"
//        sharedPrefConfig.appDetails.admobRewardAd = "ca-app-pub-3940256099942544/5224354917"
//        sharedPrefConfig.appDetails.admobInterstitialAd = "ca-app-pub-3940256099942544/1033173712"
    }


    override fun gotFirebaseResponse(firebaseRemoteConfig: FirebaseRemoteConfig) {


        if (sharedPrefConfig.appDetails.adStatus == STATUS.ON.name) {
            setAdStatusBasedOnPurchaseOrArgument()
        }

        Logger.e("TESTINGTAG", "Half customTimer  1111  ${sharedPrefConfig.appDetails.admobNativeAd}")

    }

    override fun openHowToUseActivity() {
//        val intent = Intent(runningActivity, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        startActivity(intent)
//        runningActivity!!.finish()
    }


    override fun openExtraStartActivity() {
//        val intent = Intent(runningActivity, StartActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        startActivity(intent)
//        runningActivity!!.finish()
    }

    fun showExitActivityOrDialog() {
        if (sharedPrefConfig.appDetails.whichScreenToGo != "" && TextUtils.isDigitsOnly(
                sharedPrefConfig.appDetails.whichScreenToGo
            )
        ) {
            var count = 0
            try {
                count = sharedPrefConfig.appDetails.whichScreenToGo.toInt()
            } catch (e: Exception) {
                count = 0
                e.printStackTrace()
            }
            if (count >= 2) {
                openHomeActivity()
//                val intent = Intent(runningActivity, ExitActivity::class.java)
//                startActivity(intent)
            } else if (count >= 1) {
//                val intent = Intent(runningActivity, ExitActivity::class.java)
//                startActivity(intent)
            } else {
                showExitDialog(ADTYPE.BANNER)
                //                messagingCallback.openHomeActivity();
            }
        } else {
            showExitDialog(ADTYPE.BANNER)
            //          messagingCallback.openHomeActivity();
        }
    }

    var dialogExit: Dialog? = null

    fun showExitDialog(type: ADTYPE) {
        if (dialogExit?.isShowing == true) return

        runningActivity?.let { activity ->
            dialogExit = Dialog(activity).apply {
                setContentView(R.layout.layout_dialog_exit)
                window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                setCancelable(true)

                val txtPositive = findViewById<TextView>(R.id.dialogPositiveButton)
                val txtNegative = findViewById<TextView>(R.id.dialogNegativeButton)
                val mediumBanner = findViewById<BannerView>(R.id.mediumBanner)
                val myAdViewBig = findViewById<NativeBigView>(R.id.myAdViewBig)

                txtPositive.setOnClickListener {
                    cancel()
                    activity.finishAffinity()
                }
                txtNegative.setOnClickListener {
                    cancel()
                }

                if (!activity.isFinishing) {
                    show()
                    when (type) {
                        ADTYPE.NATIVE -> {
                            mediumBanner.visibility = View.GONE
                            myAdViewBig.visibility = View.VISIBLE
                            vanillaPlayerProAdPlacer?.nativeAdManager?.callBigOnly(activity, myAdViewBig, object : AdCallback {
                                override fun adDisplayedCallback(displayed: Boolean) {

                                }
                            })
                        }

                        else -> {
                            mediumBanner.visibility = View.VISIBLE
                            myAdViewBig.visibility = View.GONE
                            mediumBanner.loadAd(activity)
                        }
                    }
                }
            }
        } ?: run {
            Logger.e("showExitDialog", "Running activity is null, cannot show dialog.")
        }
    }


    companion object {
        var context: Context? = null
        var vanillaProApp: VanillaProApp? = null


        fun setAdStatusBasedOnPurchaseOrArgument() {
            // Check if the app is purchased
            val isAppPurchased = runningActivity?.let { GlobalPreferences().getIsAppPurchased(it, false) }
            if (isAppPurchased == true) {
                sharedPrefConfig.appDetails = sharedPrefConfig.appDetails.copy(adStatus = "OFF")
            } else {
                sharedPrefConfig.appDetails = sharedPrefConfig.appDetails.copy(adStatus = "ON")
            }
        }


    }
}
