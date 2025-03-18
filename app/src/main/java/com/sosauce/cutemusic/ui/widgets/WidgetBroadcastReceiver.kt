package com.sosauce.cutemusic.ui.widgets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sosauce.cutemusic.utils.WIDGET_ACTION_BROADCAST

class WidgetBroadcastReceiver : BroadcastReceiver() {

    private var callback: WidgetCallback? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        println("broadcast widget action: ${intent?.action}")
        val action = intent?.extras?.getString(WIDGET_ACTION_BROADCAST) ?: return

        when (action) {
            WIDGET_ACTION_PLAYORPAUSE -> callback?.playOrPause()
            WIDGET_ACTION_SKIP_NEXT -> callback?.skipToNext()
            WIDGET_ACTION_SKIP_PREVIOUS -> callback?.skipToPrevious()
        }
    }

    fun startCallback(callback: WidgetCallback) {
        this.callback = callback
    }

    fun stopCallback() {
        this.callback = null
    }
}