package com.sosauce.cutemusic.ui.screens.main.components

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.ui.customs.formatBinarySize
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.utils.ImageUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun BottomSheetContent(
    music: MediaItem,
    onNavigate: (Screen) -> Unit,
    onDismiss: () -> Unit,
    onLoadMetadata: ((String) -> Unit)? = null,
) {
    val context = LocalContext.current
    val fileBitrate =
        getFileBitrate(context, Uri.parse(music.mediaMetadata.extras?.getString("uri")))
    val fileType =
        context.contentResolver.getType(Uri.parse(music.mediaMetadata.extras?.getString("uri")))
    val uri = Uri.parse(music.mediaMetadata.extras?.getString("uri"))
    val path = music.mediaMetadata.extras?.getString("path")

    val deleteSongLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                Toast.makeText(
                    context,
                    context.resources.getText(R.string.deleting_song_OK),
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                Toast.makeText(
                    context,
                    context.resources.getText(R.string.error_deleting_song),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    Column {
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
            colors = CardDefaults.cardColors(
                MaterialTheme.colorScheme.surfaceContainerHighest.copy(
                    alpha = 0.5f
                )
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = ImageUtils.imageRequester(
                        img = music.mediaMetadata.artworkUri,
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
                    CuteText(
                        text = music.mediaMetadata.title.toString(),
                        modifier = Modifier
                            .basicMarquee()
                    )
                    CuteText(
                        text = music.mediaMetadata.artist.toString(),
                        color = MaterialTheme.colorScheme.onBackground.copy(0.85f),
                        modifier = Modifier.basicMarquee()
                    )
                }
            }
        }

        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
        ) {
            Card(
                modifier = Modifier
                    .padding(bottom = 5.dp)
                    .weight(1f)
                    .clip(
                        RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 4.dp,
                            bottomStart = 24.dp,
                            bottomEnd = 4.dp
                        )
                    )
                    .clickable {
                        onLoadMetadata?.let { it(path.toString()) }
                        onDismiss()
                        onNavigate(Screen.MetadataEditor(music.mediaId))
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(
                        alpha = 0.5f
                    )
                ),
                shape = RoundedCornerShape(
                    topStart = 4.dp,
                    topEnd = 4.dp,
                    bottomStart = 24.dp,
                    bottomEnd = 4.dp
                ),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CuteText(
                        text = stringResource(R.string.edit),
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
            Card(
                modifier = Modifier
                    .padding(start = 5.dp)
                    .weight(1f)
                    .clip(
                        RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 4.dp,
                            bottomStart = 4.dp,
                            bottomEnd = 24.dp
                        )
                    )
                    .clickable {
                        createDeleteRequest(
                            uri,
                            deleteSongLauncher,
                            context
                        )
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(
                        alpha = 0.5f
                    )
                ),
                shape = RoundedCornerShape(
                    topStart = 4.dp,
                    topEnd = 4.dp,
                    bottomStart = 4.dp,
                    bottomEnd = 24.dp
                ),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CuteText(
                        text = stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        ) {
            CuteText(
                text = "${stringResource(id = R.string.size)}: ${
                    music.mediaMetadata.extras?.getLong(
                        "size"
                    )?.formatBinarySize()
                }",
                modifier = Modifier.padding(bottom = 5.dp)
            )
            CuteText(
                text = "${stringResource(id = R.string.bitrate)}: $fileBitrate",

                modifier = Modifier.padding(bottom = 5.dp)
            )
            CuteText(
                text = "${stringResource(id = R.string.type)}: $fileType",

                modifier = Modifier.padding(bottom = 5.dp)
            )
        }

    }
}

private fun getFileBitrate(context: Context, uri: Uri): String {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(context, uri)
        val bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
        bitrate?.toInt()?.div(1000)?.toString()?.plus(" kbps") ?: "Unknown"
    } catch (e: Exception) {
        "Unknown"
    } finally {
        retriever.release()
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
                "Error trying to delete song: ${e.message} ${e.stackTrace.joinToString()}"
            )
        }
    }
}

