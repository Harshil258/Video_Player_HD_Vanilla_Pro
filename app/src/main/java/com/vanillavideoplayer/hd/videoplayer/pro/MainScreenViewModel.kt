package com.vanillavideoplayer.hd.videoplayer.pro

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.review.ReviewManagerFactory
import com.vanillavideoplayer.videoplayer.core.data.repo.PrefRepo
import com.vanillavideoplayer.videoplayer.core.model.ApplicationPrefData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface MainActUiInter {
    data object Loading : MainActUiInter
    data class Success(val preferences: ApplicationPrefData) : MainActUiInter
}

@HiltViewModel
class MainActViewModel @Inject constructor(
    prefRepo: PrefRepo
) : ViewModel() {
    val uiStateVal = prefRepo.applicationPrefData.map { preferences ->
        MainActUiInter.Success(preferences)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainActUiInter.Loading
    )


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
            flow.addOnCompleteListener {
                onComplete?.invoke()
            }
        } else {
            onComplete?.invoke()
        }
    }.addOnFailureListener {
        onComplete?.invoke()
    }
}
