package com.sosauce.cutemusic.domain.repository

import android.database.ContentObserver
import android.os.Handler
import android.os.Looper

class MediaStoreObserver(
    private val onMediaStoreChanged: () -> Unit
) : ContentObserver(Handler(Looper.getMainLooper())) {

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        onMediaStoreChanged()
    }
}