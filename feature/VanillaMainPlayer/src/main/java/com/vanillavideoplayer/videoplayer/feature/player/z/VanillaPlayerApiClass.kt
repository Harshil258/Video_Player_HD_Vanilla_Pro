package com.vanillavideoplayer.videoplayer.feature.player.z

import android.content.Intent
import android.net.Uri
import androidx.media3.common.C
import com.vanillavideoplayer.videoplayer.feature.player.VanillaPlayerActivityPro
import com.vanillavideoplayer.videoplayer.feature.player.model.SubDataModel
import com.vanillavideoplayer.videoplayer.feature.player.x.returnParcelableUriArray

class VanillaPlayerApiClass(val activity: VanillaPlayerActivityPro) {

    private val intentExtras = activity.intent.extras
    val boolApiGranted: Boolean get() = intentExtras != null
    val boolPositionAllocated: Boolean get() = intentExtras?.containsKey(VAPI_POS) == true

    fun returnResult(boolPlaybackComplete: Boolean, duration: Long, position: Long): Intent {
        return Intent(VAPI_RESULT_INTENT_DATA).apply {
            if (boolPlaybackComplete) {
                putExtra(VAPI_ENDS_IN, VAPI_END_BY_COMPLETION)
            } else {
                putExtra(VAPI_ENDS_IN, VAPI_END_BY_USER)
                if (duration != C.TIME_UNSET) putExtra(VAPI_DURATION_LONG, duration.toInt())
                if (position != C.TIME_UNSET) putExtra(VAPI_POS, position.toInt())
            }
        }
    }

    val boolTitleAvailable: Boolean get() = intentExtras?.containsKey(VAPI_HEADER) == true
    val checkIfCanReturnResult: Boolean get() = intentExtras?.containsKey(VAPI_RETURN_RESPONSE) == true
    val pos: Int? get() = if (boolPositionAllocated) intentExtras?.getInt(VAPI_POS) else null

    fun returnSubs(): List<SubDataModel> {
        if (intentExtras == null) return emptyList()
        if (!intentExtras.containsKey(VAPI_SUBS_DATA)) return emptyList()

        val subs = intentExtras.returnParcelableUriArray(VAPI_SUBS_DATA) ?: return emptyList()
        val subsName = intentExtras.getStringArray(VAPI_SUBS_NAME)

        val subsEnable = intentExtras.returnParcelableUriArray(VAPI_SUB_ENABLED)
        val defaultSub = if (!subsEnable.isNullOrEmpty()) subsEnable[0] as Uri else null

        return subs.mapIndexed { index, parcelable ->
            val subtitleUri = parcelable as Uri
            val subtitleName = subsName?.let { if (it.size > index) it[index] else null }
            SubDataModel(
                subName = subtitleName,
                subUri = subtitleUri,
                boolSelected = subtitleUri == defaultSub
            )
        }
    }

    val header: String? get() = if (boolTitleAvailable) intentExtras?.getString(VAPI_HEADER) else null

    companion object {
        const val VAPI_HEADER = "title"
        const val VAPI_POS = "position"
        const val VAPI_DURATION_LONG = "duration"
        const val VAPI_RETURN_RESPONSE = "return_result"
        const val VAPI_ENDS_IN = "end_by"
        const val VAPI_SUBS_DATA = "subs"
        const val VAPI_SUB_ENABLED = "subs.enable"
        const val VAPI_SUBS_NAME = "subs.name"
        const val VAPI_RESULT_INTENT_DATA = "com.mxtech.intent.result.VIEW"

        private const val VAPI_END_BY_USER = "user"
        private const val VAPI_END_BY_COMPLETION = "playback_completion"
    }
}
