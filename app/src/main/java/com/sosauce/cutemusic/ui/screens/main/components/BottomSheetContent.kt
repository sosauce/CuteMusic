package com.sosauce.cutemusic.ui.screens.main.components

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.domain.model.Music
import com.sosauce.cutemusic.ui.customs.formatBinarySize
import com.sosauce.cutemusic.ui.customs.textCutter
import com.sosauce.cutemusic.ui.theme.GlobalFont
import com.sosauce.cutemusic.utils.ImageUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun BottomSheetContent(music: Music) {
    val context = LocalContext.current
    var art: Bitmap? by remember { mutableStateOf(null) }
    val fileType = context.contentResolver.getType(music.uri)
    val fileSize = getFileSize(context, music.uri).formatBinarySize()
    val fileBitrate = getFileBitrate(context, music.uri)


    LaunchedEffect(music.uri) {
        art = ImageUtils.getMusicArt(context, music.uri)
    }


    Column {
        val deleteImageLauncher =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult()
            ) {
                if (it.resultCode == Activity.RESULT_OK) {
                    Toast.makeText(context, context.resources.getText(R.string.deleting_song_OK), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, context.resources.getText(R.string.error_deleting_song), Toast.LENGTH_SHORT).show()
                }
            }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            shape = RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 24.dp,
                bottomStart = 24.dp,
                bottomEnd = 24.dp
            ),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = ImageUtils.imageRequester(
                        img = art,
                        context = context
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(15.dp)
                        .clip(RoundedCornerShape(15)),
                    contentScale = ContentScale.Crop

                )
                Column {
                    Text(
                        text = textCutter(music.name, 20),
                        fontFamily = GlobalFont
                    )
                    Text(
                        text = textCutter(music.artist, 20),
                        fontFamily = GlobalFont
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                    createDeleteRequest(
                        music.uri,
                        deleteImageLauncher,
                        context
                    )
                }) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = null
                    )
                }
            }
        }

            Column(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            ) {
                Text(
                    text = "${stringResource(id = R.string.size)}: $fileSize",
                    fontFamily = GlobalFont,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                Text(
                    text = "${stringResource(id = R.string.bitrate)}: $fileBitrate",
                    fontFamily = GlobalFont,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                Text(
                    text = "${stringResource(id = R.string.type)}: $fileType",
                    fontFamily = GlobalFont,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }

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

private fun getFileBitrate(context: Context, uri: Uri): String {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.use {
            retriever.setDataSource(context, uri)
            val bitrate = it.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
            bitrate?.toInt()?.div(1000)?.toString()?.plus(" kbps") ?: "Unknown"
        }
    } catch (e: Exception) {
        "Unknown"
    }
}

private fun createDeleteRequest(
    uri: Uri,
    intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>,
    context: Context
) {
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    coroutineScope.launch {
        try {
            context.contentResolver.delete(uri, null, null)
        } catch (e: SecurityException) {
            // This exception will only be thrown from Android 11
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val intentSender = MediaStore.createDeleteRequest(
                    context.contentResolver,
                    listOf(uri)
                ).intentSender

                intentSenderLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
        } catch (e: Exception) {
            Log.e(
                ContentValues.TAG,
                "Error trying to delete image: ${e.message} ${e.stackTrace.joinToString()}"
            )
        }
    }
}
