package com.sosauce.cutemusic.ui.customs

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import com.sosauce.cutemusic.domain.model.Music
import java.util.Locale

fun Long.formatBinarySize(): String {
    val kiloByteAsByte = 1.0 * 1024.0
    val megaByteAsByte = 1.0 * 1024.0 * 1024.0
    val gigaByteAsByte = 1.0 * 1024.0 * 1024.0 * 1024.0
    return when {
        this < kiloByteAsByte -> "${this.toDouble()} B"
        this >= kiloByteAsByte && this < megaByteAsByte -> "${
            String.format(
                Locale.getDefault(),
                "%.2f",
                (this / kiloByteAsByte)
            )
        } KB"

        this >= megaByteAsByte && this < gigaByteAsByte -> "${
            String.format(
                Locale.getDefault(),
                "%.2f",
                (this / megaByteAsByte)
            )
        } MB"

        else -> "Bigger than 1024 TB"
    }
}

fun Context.restart() {
    val intent = packageManager.getLaunchIntentForPackage(packageName)!!
    val componentName = intent.component!!
    val restartIntent = Intent.makeRestartActivityTask(componentName)
    startActivity(restartIntent)
    Runtime.getRuntime().exit(0)
}

fun Player.playAtIndex(
    uri: Uri,
    setState: () -> Unit
) {
    val index = (0 until mediaItemCount).indexOfFirst { getMediaItemAt(it).mediaId == uri.toString() }
    index.takeIf { it != -1 }?.let {
        seekTo(it, 0)
        play()
        setState()
    }
}

fun Music.convertToMediaItem(uri: Uri): MediaItem {
    return MediaItem.Builder()
        .setUri(uri)
        .setMediaId(uri.toString())
        .build()
}

fun MediaMetadata.artworkAsBitmap(): Bitmap? {
    return artworkData?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
}
