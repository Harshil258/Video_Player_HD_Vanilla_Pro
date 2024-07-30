package com.vanillavideoplayer.hd.videoplayer.pro

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetailsParams
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.play.core.review.ReviewManagerFactory
import com.harshil258.adplacer.utils.Logger
import com.vanillavideoplayer.videoplayer.core.data.repo.PrefRepo
import com.vanillavideoplayer.videoplayer.core.model.ApplicationPrefData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

sealed interface MainActUiInter {
    data object Loading : MainActUiInter
    data class Success(val preferences: ApplicationPrefData) : MainActUiInter
}

data class MainActState(
    val isLoading: Boolean? = null,
    val preferences: ApplicationPrefData? = null,
    val onPurchaseResponse: Int? = null
)

@HiltViewModel
class MainActViewModel @Inject constructor(
    prefRepo: PrefRepo
) : ViewModel() {

    private val _mainActState = MutableStateFlow(MainActState())
    val mainActState = _mainActState.asStateFlow()

    val uiStateVal = prefRepo.applicationPrefData.map { preferences ->
        MainActUiInter.Success(preferences)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainActUiInter.Loading
    )

    fun makePurchase(activity: Activity) {
        val skuDetailsParams = arrayListOf("vanilla_video_player_ads_free")

        var billingClient: BillingClient? = null
        val billingBuilder = BillingClient.newBuilder(activity)
            .enablePendingPurchases()
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                            val acknowledgePurchaseParams =
                                AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(purchase.purchaseToken).build()

                            billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                                val billingResponseCode = billingResult.responseCode
                                activity.runOnUiThread {
                                    if (billingResponseCode == BillingClient.BillingResponseCode.OK) {
                                        Toast.makeText(
                                            activity,
                                            "Purchase Successful",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        setIsItemPurchase(true, activity)
                                    } else {
                                        Toast.makeText(
                                            activity,
                                            "Purchase failed or cancelled",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                    if (!GlobalPreferences().getIsAppPurchased(activity, false)) {
                        setIsItemPurchase(true, activity)
                    }
                    Toast.makeText(activity, "You already own the premium!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    activity.runOnUiThread {
//                        if (GlobalPrefs().getIsAppPurchased(activity, false)) {
//                            Toast.makeText(activity, "You already have premium.", Toast.LENGTH_SHORT).show()
//                        } else {
                        Timber.tag("TAG_NEO")
                            .d("makePurchase: Billing response not OK or purchases null")
                        Toast.makeText(
                            activity,
                            "Purchase failed: Billing response not OK or purchases null",
                            Toast.LENGTH_SHORT
                        ).show()
//                        }
                    }
                }
            }

        billingClient = billingBuilder.build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                // Handle disconnection
            }

            override fun onBillingSetupFinished(p0: BillingResult) {
                if (p0.responseCode == BillingClient.BillingResponseCode.OK) {
                    val params = SkuDetailsParams.newBuilder().setSkusList(skuDetailsParams)
                        .setType(SkuType.INAPP)
                    billingClient.querySkuDetailsAsync(params.build()) { _, skuDetailsList ->
                        if (skuDetailsList.isNullOrEmpty()) {
                            Logger.e("TAG_NEO", "makePurchase: Sku details empty or null")
                            activity.runOnUiThread {
                                Toast.makeText(
                                    activity,
                                    "Failed to retrieve product details",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            for (skuDetails in skuDetailsList) {
                                val flowPurchase =
                                    BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build()
                                val responseCode = billingClient.launchBillingFlow(
                                    activity,
                                    flowPurchase
                                ).responseCode
                                Logger.d(
                                    "TAG_NEO",
                                    "makePurchase: Launch billing flow response code: $responseCode"
                                )
                                // You might want to handle response code here if needed
                            }
                        }
                    }
                } else {
                    Logger.e("TAG_NEO", "makePurchase: Billing setup failed")
                    activity.runOnUiThread {
                        Toast.makeText(activity, "Billing setup failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun setIsItemPurchase(isPurchased: Boolean, activity: Activity? = null) {
        activity?.let {
            GlobalPreferences().setIsAppPurchased(it, isPurchased)
            if (isPurchased) {
                showRestartDialog(activity)
            }
        }
        _mainActState.value = mainActState.value.copy(
            preferences = mainActState.value.preferences?.copy(isItemPurchased = isPurchased)
        )
    }


    private fun showRestartDialog(activity: Activity) {
        MaterialAlertDialogBuilder(activity).setTitle("Ads Removed")
            .setMessage("To apply changes, please restart the application.")
            .setPositiveButton("Restart") { _, _ ->
                restartApplication(activity)
            }.setCancelable(false).show()
    }

    private fun restartApplication(@NonNull activity: Activity) {
        Handler(activity.mainLooper).postDelayed({
            val pm = activity.packageManager
            val intent = pm.getLaunchIntentForPackage(activity.packageName)
            intent?.let {
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                activity.startActivity(it)
                Runtime.getRuntime().exit(0)
            }
        }, 500)
    }
}

fun launchInAppReview(
    activity: Activity,
    onComplete: (() -> Unit)? = null,
) {
    val reviewManager = ReviewManagerFactory.create(activity)
    val request = reviewManager.requestReviewFlow()

    request.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val reviewInfo = task.result
            val flow = reviewManager.launchReviewFlow(activity, reviewInfo)
            flow.addOnCompleteListener { flowTask ->
                onComplete?.invoke()
            }
        } else {
            onComplete?.invoke()
        }
    }.addOnFailureListener { exception ->
        onComplete?.invoke()
    }
}
