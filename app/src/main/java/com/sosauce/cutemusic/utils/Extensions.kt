package com.sosauce.cutemusic.utils

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch

fun ContentResolver.queryAsFlow(
    uri: Uri,
    projection: Array<String>? = null,
    selection: String? = null,
    selectionArgs: Array<String>? = null
) = callbackFlow {
    val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            launch(Dispatchers.IO) {
                runCatching {
                    trySend(query(uri, projection, selection, selectionArgs, null))
                }
            }
        }
    }

    registerContentObserver(uri, true, observer)

    launch(Dispatchers.IO) {
        runCatching {
            trySend(
                query(uri, projection, selection, selectionArgs, null)
            )
        }.onFailure {
            Log.d("CuteError", it.message.toString())
        }
    }

    awaitClose {
        unregisterContentObserver(observer)
    }

}.conflate()

fun Modifier.thenIf(
    condition: Boolean,
    modifier: Modifier
): Modifier {
    return this.then(
        if (condition) {
            modifier
        } else Modifier
    )
}