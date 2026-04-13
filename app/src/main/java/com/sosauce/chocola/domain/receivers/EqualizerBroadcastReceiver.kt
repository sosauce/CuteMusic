package com.sosauce.chocola.domain.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sosauce.chocola.presentation.screens.settings.EqualizerAction
import com.sosauce.chocola.presentation.screens.settings.EqualizerCallback
import com.sosauce.chocola.presentation.widgets.WidgetCallback
import com.sosauce.chocola.utils.EQUALIZER_ACTION_BROADCAST

class EqualizerBroadcastReceiver: BroadcastReceiver() {

    private var callback: EqualizerCallback? = null



    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.extras?.getString(EQUALIZER_ACTION_BROADCAST) ?: return


        when (action) {
            EqualizerAction.SET_NEW_GAIN -> {
                val centerFrequencyMilliHertz = intent.getIntExtra("centerFrequency", 0)
                val gain = intent.getShortExtra("gainMilliBel", 0)
                callback?.setBandGain(centerFrequencyMilliHertz, gain)
            }

            EqualizerAction.TOGGLE -> {
                val enable = intent.getBooleanExtra("enable", false)
                callback?.toggle(enable)
            }

            EqualizerAction.USE_PRESET -> {
                val presetBand = intent.getShortExtra("presetBand", 0)
                callback?.usePreset(presetBand)
            }
        }

    }

    fun startCallback(callback: EqualizerCallback) {
        this.callback = callback
    }

    fun stopCallback() {
        this.callback = null
    }
}