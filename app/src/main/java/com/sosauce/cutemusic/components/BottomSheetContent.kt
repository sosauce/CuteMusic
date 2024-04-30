package com.sosauce.cutemusic.components

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.audio.getMusicArt
import com.sosauce.cutemusic.customs.formatBinarySize
import com.sosauce.cutemusic.logic.imageRequester
import com.sosauce.cutemusic.ui.theme.GlobalFont

@Composable
fun BottomSheetContent(music: Music) {
    val context = LocalContext.current
    var art: ByteArray? by remember { mutableStateOf(byteArrayOf()) }
    val fileType = getFileType(context, music.uri)
    val fileSize = getFileSize(context, music.uri).formatBinarySize()


    LaunchedEffect(music.uri) {
        art = getMusicArt(context, music)
    }


        Column {
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "About",
                        modifier = Modifier.padding(15.dp),
                        fontFamily = GlobalFont,
                        fontSize = 18.sp
                    )
                }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                shape = RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 24.dp,
                    bottomStart = 4.dp,
                    bottomEnd = 4.dp
                ),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = imageRequester(
                            img = art ?: R.drawable.cute_music_icon,
                            context = context
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(15.dp)
                            .clip(RoundedCornerShape(15))
                    )
                    Column {
                        Text(
                            text = music.title,
                            fontFamily = GlobalFont
                        )
                        Text(
                            text = music.artist,
                            fontFamily = GlobalFont
                        )
                    }
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                shape = RoundedCornerShape(
                    topStart = 4.dp,
                    topEnd = 4.dp,
                    bottomStart = 24.dp,
                    bottomEnd = 24.dp
                ),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh),
            ) {
                Column(modifier = Modifier.padding(15.dp)) {
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
        }
    }

private fun getFileType(context: Context, uri: Uri): String {
    val type = context.contentResolver.getType(uri)

    return when(type) {
        "audio/mpeg" -> "MP3"
        "audio/ogg" -> "OGG"
        else -> "Unknown"
    }
}

private fun getFileSize(context: Context, uri: Uri): Long {
    var size: Long = 0
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        if (cursor.moveToFirst()) {
            size = cursor.getLong(sizeIndex)
        }
    }
    return size
}

