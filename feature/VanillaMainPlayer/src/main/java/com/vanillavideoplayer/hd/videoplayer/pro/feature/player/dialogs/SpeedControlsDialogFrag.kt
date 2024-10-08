package com.vanillavideoplayer.hd.videoplayer.pro.feature.player.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vanillavideoplayer.hd.videoplayer.pro.core.common.extra.roundTheFloat
import com.vanillavideoplayer.hd.videoplayer.pro.feature.player.databinding.PlaybackSpeedBinding
import com.vanillavideoplayer.hd.videoplayer.pro.core.ui.R as coreUiR

class SpeedControlsDialogFrag(
    private val currentSpeed: Float,
    private val onChange: (Float) -> Unit
) : DialogFragment() {

    private lateinit var binding: PlaybackSpeedBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = PlaybackSpeedBinding.inflate(layoutInflater)

        return activity?.let { activity ->
            binding.apply {
                speedText.text = currentSpeed.toString()
                speed.value = currentSpeed.roundTheFloat(1)
                speed.addOnChangeListener { _, _, _ ->
                    val newSpeed = speed.value.roundTheFloat(1)
                    onChange(newSpeed)
                    speedText.text = newSpeed.toString()
                }
                resetSpeed.setOnClickListener {
                    speed.value = 1.0f
                }
                incSpeed.setOnClickListener {
                    if (speed.value < 4.0f) {
                        speed.value = (speed.value + 0.1f).roundTheFloat(1)
                    }
                }
                decSpeed.setOnClickListener {
                    if (speed.value > 0.2f) {
                        speed.value = (speed.value - 0.1f).roundTheFloat(1)
                    }
                }
            }

            val builder = MaterialAlertDialogBuilder(activity)
            builder.setTitle(getString(coreUiR.string.select_playback_speed))
                .setView(binding.root)
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
