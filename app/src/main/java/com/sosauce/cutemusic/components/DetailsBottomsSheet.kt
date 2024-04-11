package com.sosauce.cutemusic.components

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.ui.theme.GlobalFont

@Composable
fun DetailsBottomSheet(
    music: Music,
    onClick: () -> Unit
) {

    val context = LocalContext.current
    val fileSize = getFileSize(context, music.uri).formatBinarySize()
    val fileType = getFileType(context, music.uri)


    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = { onClick() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "arrow back"
            )
        }

        Text(
            text = music.title,
            fontFamily = GlobalFont,
            fontSize = 20.sp
        )
    }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, top = 35.dp),
    ) {
        Text(
            text = "Artist: ${music.artist}",
            fontFamily = GlobalFont,
            modifier = Modifier.padding(bottom = 5.dp)
        )
        Text(
            text = "Album: ${music.album}",
            fontFamily = GlobalFont,
            modifier = Modifier.padding(bottom = 5.dp)
        )
        Text(
            text = "Size: $fileSize",
            fontFamily = GlobalFont,
            modifier = Modifier.padding(bottom = 5.dp)
        )
        Text(
            text = "Type: $fileType",
            fontFamily = GlobalFont,
            modifier = Modifier.padding(bottom = 5.dp)
        )
    }
}

fun getFileSize(context: Context, uri: Uri): Long {
    var size: Long = 0
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        if (cursor.moveToFirst()) {
            size = cursor.getLong(sizeIndex)
        }
    }
    return size
}

fun Long.formatBinarySize(): String {
    val kiloByteAsByte = 1.0 * 1024.0
    val megaByteAsByte = 1.0 * 1024.0 * 1024.0
    val gigaByteAsByte = 1.0 * 1024.0 * 1024.0 * 1024.0
    return when {
        this < kiloByteAsByte -> "${this.toDouble()} B"
        this >= kiloByteAsByte && this < megaByteAsByte -> "${String.format("%.2f", (this / kiloByteAsByte))} KB"
        this >= megaByteAsByte && this < gigaByteAsByte -> "${String.format("%.2f", (this / megaByteAsByte))} MB"
        else -> "Bigger than 1024 TB"
    }
}

fun getFileType(context: Context, uri: Uri): String {
    val type = context.contentResolver.getType(uri)

    return when(type) {
        "audio/mpeg" -> "MP3"
        "audio/ogg" -> "OGG"
        else -> "Unknown"
    }
}