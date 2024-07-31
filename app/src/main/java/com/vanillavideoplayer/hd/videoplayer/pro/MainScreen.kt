package com.vanillavideoplayer.hd.videoplayer.pro

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.harshil258.adplacer.adViews.BannerView
import com.harshil258.adplacer.utils.Constants.adPlacerApplication
import com.harshil258.adplacer.utils.Logger
import com.harshil258.adplacer.utils.STATUS
import com.harshil258.adplacer.utils.SharedPrefConfig.Companion.sharedPrefConfig
import com.vanillavideoplayer.hd.videoplayer.pro.VanillaProApp.Companion.setAdStatusBasedOnPurchaseOrArgument
import com.vanillavideoplayer.hd.videoplayer.pro.compose.MAIN_ROUTE_CONST
import com.vanillavideoplayer.hd.videoplayer.pro.compose.MainScreenUi
import com.vanillavideoplayer.hd.videoplayer.pro.compose.RatingDialogVideoPro
import com.vanillavideoplayer.videoplayer.core.media.sync.MediaSynchronizerInter
import com.vanillavideoplayer.videoplayer.core.model.ThemeConfigEnum
import com.vanillavideoplayer.videoplayer.core.ui.theme.VideoPlayerTheme
import com.vanillavideoplayer.videoplayer.feature.player.PlayerViewModel
import com.vanillavideoplayer.videoplayer.feature.videopicker.navigation.settingsNavGraph
import com.vanillavideoplayer.videoplayer.feature.videopicker.screens.VideosStateSealedInter
import com.vanillavideoplayer.videoplayer.feature.videopicker.screens.media.FilePickerViewModel
import com.vanillavideoplayer.hd.videoplayer.pro.google_iab.BillingConnector
import com.vanillavideoplayer.hd.videoplayer.pro.google_iab.BillingEventListener
import com.vanillavideoplayer.hd.videoplayer.pro.google_iab.enums.ProductType
import com.vanillavideoplayer.hd.videoplayer.pro.google_iab.models.BillingResponse
import com.vanillavideoplayer.hd.videoplayer.pro.google_iab.models.ProductInfo
import com.vanillavideoplayer.hd.videoplayer.pro.google_iab.models.PurchaseInfo
import com.vanillavideoplayer.hd.videoplayer.pro.notifications.MediaStoreBroadcastReceiverPro
import com.vanillavideoplayer.hd.videoplayer.pro.notifications.MediaStoreScheduler
import com.vanillavideoplayer.videoplayer.settings.navigation.langPrefNavRoute
import com.vanillavideoplayer.videoplayer.settings.navigation.langPrefScreen
import com.vanillavideoplayer.videoplayer.settings.navigation.navToOnboardingPref
import com.vanillavideoplayer.videoplayer.settings.navigation.navigateToSettings
import com.vanillavideoplayer.videoplayer.settings.navigation.onboardingPrefScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var isAppPurchased = mutableStateOf(false)
    private var formattedPrice = mutableStateOf("")

    private lateinit var billingConnector: BillingConnector

    private var showDialog = mutableStateOf(false)

    private val purchasedInfoList = mutableListOf<PurchaseInfo>()
    private val fetchedProductInfoList = mutableListOf<ProductInfo>()


    private val sharedPreferencesListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "count") {
                val count = GlobalPreferences().getCount(this)
                if (count == 5) {
                    showDialog.value = true
                    GlobalPreferences().setCount(this, 0)
                }
            } else if (key == "IsAppPurchased") {
                isAppPurchased.value = GlobalPreferences().getIsAppPurchased(this@MainActivity)
            }
        }

    @Inject
    lateinit var syncer: MediaSynchronizerInter

    private val viewModel: MainActViewModel by viewModels()
    private val vanillaPlayerViewModel: PlayerViewModel by viewModels()
    private val filePickerViewModel: FilePickerViewModel by viewModels()

    private val storagePermissionList = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> Manifest.permission.READ_MEDIA_VIDEO
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> Manifest.permission.READ_EXTERNAL_STORAGE
        else -> Manifest.permission.READ_EXTERNAL_STORAGE
    }
    private val notificationPermissionList = Manifest.permission.POST_NOTIFICATIONS

    private val PRODUCT_ID = "vanilla_video_player_ads_free"

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Logger.e("TESTINGTAG1212", "MainACtiovituy   onCreate : adStatus    ${sharedPrefConfig.appDetails.adStatus}")

        var uiStateVal: MainActUiInter by mutableStateOf(MainActUiInter.Loading)


        if (sharedPrefConfig.appDetails.adStatus == STATUS.ON.name) {
            setAdStatusBasedOnPurchaseOrArgument()
        }

        val scheduler = MediaStoreScheduler(this)
        scheduler.scheduleMediaStoreObserver()

        GlobalPreferences().setListener(this, sharedPreferencesListener)
        GlobalPreferences().increaseCount(applicationContext)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateVal.collect { state ->
                    uiStateVal = state
                }
            }
        }


        val intent = Intent(this, MediaStoreBroadcastReceiverPro::class.java)
        sendBroadcast(intent)


        val nonConsumableIds = mutableListOf<String>()
        nonConsumableIds.add(PRODUCT_ID)

        billingConnector = BillingConnector(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAstSHjBk+yUv1ZHp8cxtyth4NdwfJj93hDkKgchxfv9Qwu8gF86j6/Zidx+En2IlvGfk1VuRYY+fqZvYtCqNj+u/7vYBAs05UWsNNld1yL/rNKPuIzdA+5uqHUKNjGgOlzLNjmga5LUMatV7yIDhW2ugZPgUSNzQ8mrgMkeZQkOpTjwaxZ5wKegBsSTOg2wzZxQrbLebgrNIMZ4nI1WKyCKgvM77UnzSaGzC145jRS++K7dtxT+I/IziIgLa43f4op2+lw5eBiMCV/vFZibt9fjJiSnbfEG0YMuR8fdnNcOExaswvDTYO40zjbALtiyzfTI21cjfcivyV62pk7Dh+BwIDAQAB")
            .setNonConsumableIds(nonConsumableIds)
            .autoAcknowledge()
            .autoConsume()
            .enableLogging()
            .connect()

        adPlacerApplication.showOneSignalNotificationPrompt()
//        adPlacerApplication.subscribeToTheTopic("ONESIGNAL_TESTING")
        adPlacerApplication.subscribeToTheTopic("MOVIE_REMINDER")

        isAppPurchased.value = GlobalPreferences().getIsAppPurchased(this@MainActivity)

        installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        initBillingConnection()

        setContent {
            VideoPlayerTheme(
                darkTheme = usDarkThemeOrNot(uiState = uiStateVal),
                highContrastDarkTheme = useHighContrastDarkThemeOrNot(uiState = uiStateVal),
                dynamicColor = useDynamicThemingOrNot(uiState = uiStateVal)
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface
                ) {
                    val notificationPermissionState =
                        rememberPermissionState(permission = notificationPermissionList)
                    val storagePermissionState =
                        rememberPermissionState(permission = storagePermissionList)
                    val lifecycleOwner = LocalLifecycleOwner.current
                    val shouldShowLanguageScreen = remember {
                        mutableStateOf(
                            GlobalPreferences().getIsLanguageFirstTime(this@MainActivity)
//                            true
                        )
                    }
                    DisposableEffect(key1 = lifecycleOwner) {
                        val observer = LifecycleEventObserver { _, event ->
                            if (event == Lifecycle.Event.ON_START) {
                                storagePermissionState.launchPermissionRequest()
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    notificationPermissionState.launchPermissionRequest()
                                }
                            }
                        }
                        lifecycleOwner.lifecycle.addObserver(observer)
                        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
                    }

                    LaunchedEffect(key1 = storagePermissionState.status.isGranted) {
                        if (storagePermissionState.status.isGranted) {
                            syncer.startSynchronizing()
                        }
                    }

                    val mainNavController = rememberNavController()
                    val mediaNavController = rememberNavController()
                    val androidViewBannerCache = remember { mutableMapOf<Int, BannerView>() }

                    if (!shouldShowLanguageScreen.value) {
                        NavHost(
                            navController = mainNavController, startDestination = MAIN_ROUTE_CONST
                        ) {
                            settingsNavGraph(
                                navController = mainNavController,
                                onPurchase = {
                                    makePurchase()
                                }, formattedPrice = formattedPrice, isPurchased = isAppPurchased
                            )

                            composable(MAIN_ROUTE_CONST) {
//                            if(!mainScreenLoaded){
                                MainScreenUi(permissionState = storagePermissionState,
                                    mainNavController = mainNavController,
                                    mediaNavController = mediaNavController,
                                    filePickerViewModel = filePickerViewModel,
                                    playerViewModel = vanillaPlayerViewModel,
                                    androidViewBannerCache = androidViewBannerCache,
                                    onSettingClick = {
                                        mainNavController.navigateToSettings()
                                    })
//                            }
                            }
                        }
                    } else {
                        NavHost(
                            navController = mainNavController, startDestination = langPrefNavRoute
                        ) {
                            langPrefScreen(shouldShowBackNavigation = false) {
                                mainNavController.navToOnboardingPref()
                            }
                            onboardingPrefScreen {
                                GlobalPreferences().setIsLanguageFirstTime(this@MainActivity, false)
                                shouldShowLanguageScreen.value = false
                            }
                        }
                    }

                    if (showDialog.value) {
                        if (sharedPrefConfig.appDetails.isDefaultInAppRating == "ON") {
                            launchInAppReview(this@MainActivity) {
                                showDialog.value = false
                            }
                        } else {
                            RatingDialogVideoPro(onDismiss = {
                                showDialog.value = false
                            }) { rating ->
                                if (rating >= 4) {
//                                    openLinkInBrowser(this@MainActivity, "https://play.google.com/store/apps/details?id=com.vanillavideoplayer.videoplayer")
                                    launchInAppReview(this@MainActivity) {
                                        showDialog.value = false
                                    }
                                } else {
                                    val recipient = "flavourlessdevelopers@gmail.com"
                                    val subject =
                                        "We Value Your Feedback on Vanilla Video Player - Let's Improve Together!"
                                    val body = """
                                        Enter all of your feedback here:
                                        ${getDeviceDetails(this@MainActivity)}
                                        """.trimIndent()
                                    val uriText = "mailto:$recipient" +
                                            "?subject=" + Uri.encode(subject) +
                                            "&body=" + Uri.encode(body)

                                    val uri = Uri.parse(uriText)
                                    val emailIntent = Intent(Intent.ACTION_SENDTO, uri)
                                    startActivity(emailIntent)
                                    showDialog.value = false
                                }
                            }
                        }
                    }
                }
            }
        }

        initObserver()
    }

    private fun initObserver() {
        lifecycleScope.launch(Dispatchers.Default) {
            filePickerViewModel.recentVideoState.collect { state ->
                when (state) {
                    is VideosStateSealedInter.SuccessDataClass -> {
                        val lastPlayedMovies = state.data.filter {
                            var playedPercentage = 0
                            val currentDuration = vanillaPlayerViewModel.getVideoState(it.path)?.position?.toInt()
                            if (currentDuration != null) {
                                playedPercentage = ((currentDuration.toFloat() / it.duration.toFloat()) * 100).toInt()
                            }
                            Logger.d("TAG", "initObserver: $playedPercentage , ${(it.duration > (90 * 60 * 1000))}")
                            (it.duration > (60 * 60 * 1000)) && (playedPercentage >= 30)
                        }.take(5)
                        GlobalPreferences().setLastPlayedVideos(this@MainActivity, lastPlayedMovies)
                        Logger.d("TAG", "initObserver: $lastPlayedMovies")
                    }

                    else -> {}
                }
            }
        }
    }

    private fun getDeviceDetails(context: Context): String {
        val deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"
        val androidVersion = "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"

        val packageManager = context.packageManager
        val packageName = context.packageName
        val versionName: String
        val versionCode: Long

        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            versionName = packageInfo.versionName
            versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return "Error retrieving app version"
        }

        val appVersion = "App Version: $versionName ($versionCode)"

        return """
        Device Name: $deviceName
        Android Version: $androidVersion
        $appVersion
    """.trimIndent()
    }

    private fun checkPurchaseHistory() {
//        lifecycleScope.launch {
//            val result = billingClient?.queryPurchaseHistory(
//                QueryPurchaseHistoryParams.newBuilder().setProductType(ProductType.INAPP).build()
//            )
//
//            val purchaseHistoryRecords = result?.purchaseHistoryRecordList
//            isPurchased.value = !purchaseHistoryRecords.isNullOrEmpty()
//            GlobalPrefs().setIsAppPurchased(
//                this@MainActivity, isPurchased.value
//            )

//            if (purchaseHistoryRecords.isNullOrEmpty()) {
//                Log.e("CASSample124234", "checkPurchaseHistory: No purchase history records found.")
//            } else {
//                Log.e("CASSample124234", "checkPurchaseHistory: Purchase history records found.")
//                for (record in purchaseHistoryRecords) {
//                    Log.e("CASSample124234", "Purchase Record: ${record.purchaseToken}")
//                    Log.e("CASSample124234", "Purchase Time: ${record.purchaseTime}")
//                    Log.e("CASSample124234", "Purchase Original Json: ${record.originalJson}")
//                    Log.e("CASSample124234", "Purchase Signature: ${record.signature}")
//                    Log.e("CASSample124234", "Purchase Sku: ${record.products.toSet()}")
//                    // Add more fields if needed based on PurchaseHistoryRecord class details
//                }
//            }
//
//            Log.e("CASSample124234", "checkPurchaseHistory: isPurchased = ${isPurchased.value}")
//        }
    }


    /*private fun checkPurchaseHistory() {
        lifecycleScope.launch {
            val result = billingClient?.queryPurchaseHistory(
                QueryPurchaseHistoryParams.newBuilder().setProductType(ProductType.INAPP).build()
            )
            isPurchased.value = !result?.purchaseHistoryRecordList.isNullOrEmpty()
            GlobalPrefs().setIsAppPurchased(
                this@MainActivity, isPurchased.value
            )
            Log.e("CASSample124234", "checkPurchaseHistory: ${isPurchased.value}")
        }
    }*/

    private fun makePurchase() {
        billingConnector.purchase(this, PRODUCT_ID)
    }


    private fun initBillingConnection() {

        billingConnector.setBillingEventListener(object : BillingEventListener {
            override fun onProductsFetched(productDetails: MutableList<ProductInfo>) {
                var product: String
                var price: String

                for (productInfo in productDetails) {
                    product = productInfo.product
                    price = productInfo.oneTimePurchaseOfferFormattedPrice
                    if (product.equals(PRODUCT_ID, ignoreCase = true)) {
                        Logger.d("BillingConnector", "Product fetched: $product")
                        Logger.d("BillingConnector", "Product price: $price")
                        formattedPrice.value = price ?: ""
                    }
                    fetchedProductInfoList.add(productInfo) //check "usefulPublicMethods" to see how to synchronously check a purchase state
                }
            }


            override fun onPurchasedProductsFetched(
                productType: ProductType,
                purchases: MutableList<PurchaseInfo>
            ) {
                // Initialize the flag to false
                var isProductPurchased = false

                // Iterate over the purchases list
                purchases.forEach {
                    if (it.product == PRODUCT_ID) {
                        // Update the ad status and the purchase flag
                        Log.e("BillingConnector", "onPurchasedProductsFetched:   got the purchased product")
                        GlobalPreferences().setIsAppPurchased(this@MainActivity, true)
                        setAdStatusBasedOnPurchaseOrArgument()
                        isProductPurchased = true
                    }
                }

                // If the product ID was not found, set the purchase flag to false
                if (!isProductPurchased) {
                    GlobalPreferences().setIsAppPurchased(this@MainActivity, false)
                    setAdStatusBasedOnPurchaseOrArgument()
                }
            }


            override fun onProductsPurchased(purchases: MutableList<PurchaseInfo>) {
                var product: String
                var isProductPurchased = false

                for (purchaseInfo in purchases) {
                    product = purchaseInfo.product

                    if (product.equals(PRODUCT_ID, ignoreCase = true)) {
                        GlobalPreferences().setIsAppPurchased(
                            this@MainActivity, true
                        )
                        setAdStatusBasedOnPurchaseOrArgument()
                        isProductPurchased = true

                    }
                    purchasedInfoList.add(purchaseInfo) //check "usefulPublicMethods" to see how to acknowledge or consume a purchase manually
                }
                if (!isProductPurchased) {
                    GlobalPreferences().setIsAppPurchased(this@MainActivity, false)
                    setAdStatusBasedOnPurchaseOrArgument()
                }
            }

            override fun onPurchaseAcknowledged(purchase: PurchaseInfo) {
                when (purchase.product) {
                    PRODUCT_ID -> {
                        Toast.makeText(
                            this@MainActivity,
                            "Enjoy your ad free experience!",
                            Toast.LENGTH_SHORT
                        ).show()
                        GlobalPreferences().setIsAppPurchased(
                            this@MainActivity, true
                        )
                    }
                }
            }

            override fun onPurchaseConsumed(purchase: PurchaseInfo) {
            }

            override fun onBillingError(
                billingConnector: BillingConnector,
                response: BillingResponse
            ) {
            }
        })


        for (purchaseInfo in purchasedInfoList) {
            billingConnector.consumePurchase(purchaseInfo)
        }
        for (purchaseInfo in purchasedInfoList) {
            billingConnector.acknowledgePurchase(purchaseInfo)
        }


//        billingClient =
//            BillingClient.newBuilder(this@MainActivity).setListener(purchasesUpdatedListener)
//                .enablePendingPurchases().build()
//
//        billingClient?.startConnection(object : BillingClientStateListener {
//            override fun onBillingSetupFinished(billingResult: BillingResult) {
//                if (billingResult.responseCode == BillingResponseCode.OK) {
//                    lifecycleScope.launch(Dispatchers.IO) {
//                        checkPurchaseHistory()
//                        getProductDetails()
//                    }
//                }
//            }
//
//            override fun onBillingServiceDisconnected() {
//            }
//        })
    }

    private suspend fun getProductDetails() {
//        val productList = listOf(
//            QueryProductDetailsParams.Product.newBuilder().setProductId(PRODUCT_ID)
//                .setProductType(ProductType.INAPP).build()
//        )
//
//        val params = QueryProductDetailsParams.newBuilder()
//        params.setProductList(productList)
//
//        val productDetailsResult = withContext(Dispatchers.IO) {
//            billingClient?.queryProductDetails(params.build())
//        }

//        productDetails = productDetailsResult?.productDetailsList?.firstOrNull()
//        formattedPrice.value = productDetails?.oneTimePurchaseOfferDetails?.formattedPrice ?: ""
    }
}

@Composable
fun useHighContrastDarkThemeOrNot(
    uiState: MainActUiInter
): Boolean = when (uiState) {
    MainActUiInter.Loading -> false
    is MainActUiInter.Success -> uiState.preferences.useHighContrastDarkTheme
}

@Composable
fun useDynamicThemingOrNot(
    uiState: MainActUiInter
): Boolean = when (uiState) {
    MainActUiInter.Loading -> false
    is MainActUiInter.Success -> uiState.preferences.useDynamicColors
}

@Composable
private fun usDarkThemeOrNot(
    uiState: MainActUiInter
): Boolean = when (uiState) {
    MainActUiInter.Loading -> isSystemInDarkTheme()
    is MainActUiInter.Success -> when (uiState.preferences.themeConfigEnum) {
        ThemeConfigEnum.THEME_SYSTEM -> isSystemInDarkTheme()
        ThemeConfigEnum.THEME_OFF -> false
        ThemeConfigEnum.THEME_ON -> true
    }
}