package com.sosauce.chocola.presentation.widgets

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.sosauce.chocola.utils.PACKAGE
import com.sosauce.chocola.utils.WIDGET_ACTION_BROADCAST
import kotlin.random.Random

const val WIDGET_ACTION_SKIP_PREVIOUS = "WIDGET_ACTION_SKIP_PREVIOUS"
const val WIDGET_ACTION_PLAYORPAUSE = "WIDGET_ACTION_SKIP_PLAYORPAUSE"
const val WIDGET_ACTION_SKIP_NEXT = "WIDGET_ACTION_SKIP_NEXT"

fun createWidgetPendingIntent(
    context: Context,
    widgetActions: String
): PendingIntent {
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        Random.nextInt(),
        Intent(PACKAGE).putExtra(
            WIDGET_ACTION_BROADCAST,
            widgetActions
        ),
        PendingIntent.FLAG_IMMUTABLE

    )

    return pendingIntent
}


interface WidgetCallback {
    fun skipToNext()
    fun playOrPause()
    fun skipToPrevious()
}