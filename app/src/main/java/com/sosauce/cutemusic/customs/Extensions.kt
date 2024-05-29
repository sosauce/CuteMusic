package com.sosauce.cutemusic.customs

import android.content.Context
import android.content.Intent

fun Context.restart() {
    val packageManager = packageManager
    val intent = packageManager.getLaunchIntentForPackage(packageName)!!
    val componentName = intent.component!!
    val restartIntent = Intent.makeRestartActivityTask(componentName)
    startActivity(restartIntent)
    Runtime.getRuntime().exit(0)
}