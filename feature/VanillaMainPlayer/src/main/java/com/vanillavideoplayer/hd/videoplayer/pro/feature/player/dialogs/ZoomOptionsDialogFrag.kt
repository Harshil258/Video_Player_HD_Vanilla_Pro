package com.vanillavideoplayer.hd.videoplayer.pro.feature.player.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.media3.common.util.UnstableApi
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vanillavideoplayer.hd.videoplayer.pro.core.model.VidZoomEnum
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.R

@UnstableApi
class ZoomOptionsDialogFrag(
    private val defaultVidZoomEnum: VidZoomEnum,
    private val onVideoZoomOptionSelected: (vidZoomEnum: VidZoomEnum) -> Unit
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val vidZoomEnumValues = VidZoomEnum.entries.toTypedArray()

        return activity?.let { activity ->
            MaterialAlertDialogBuilder(activity)
                .setTitle(getString(R.string.video_zoom))
                .setSingleChoiceItems(
                    vidZoomEnumValues.map { getString(it.nameRes()) }.toTypedArray(),
                    vidZoomEnumValues.indexOfFirst { it == defaultVidZoomEnum }
                ) { dialog, trackIndex ->
                    onVideoZoomOptionSelected(vidZoomEnumValues[trackIndex])
                    dialog.dismiss()
                }.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

fun VidZoomEnum.nameRes(): Int {
    val stringRes = when (this) {
        VidZoomEnum.VANILLA_FIT -> R.string.best_fit
        VidZoomEnum.VANILLA_STRETCH -> R.string.stretch
        VidZoomEnum.VANILLA_CROP -> R.string.crop
        VidZoomEnum.VANILLA_SOO_TAKA -> R.string.hundred_percent
    }

    return stringRes
}
