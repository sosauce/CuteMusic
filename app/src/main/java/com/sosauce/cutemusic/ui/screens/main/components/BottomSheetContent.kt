package com.sosauce.cutemusic.ui.screens.main.components

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.ui.customs.formatBinarySize
import com.sosauce.cutemusic.ui.customs.textCutter
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.theme.GlobalFont
import com.sosauce.cutemusic.utils.ImageUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun BottomSheetContent(
    music: MediaItem,
    onNavigate: (Screen) -> Unit,
    onDismiss: () -> Unit,

) {
    val context = LocalContext.current
    val fileBitrate = getFileBitrate(context, Uri.parse(music.mediaMetadata.extras?.getString("uri")))
    val fileType = context.contentResolver.getType(Uri.parse(music.mediaMetadata.extras?.getString("uri")))

    val deleteSongLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                Toast.makeText(context, context.resources.getText(R.string.deleting_song_OK), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, context.resources.getText(R.string.error_deleting_song), Toast.LENGTH_SHORT).show()
            }
        }

    val favSongLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                Toast.makeText(context, "You like this song don't you!" , Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "A problem occurred ! Guess you can't like this song :(", Toast.LENGTH_SHORT).show()
            }
        }

    Column {

        Row {
//            Column(
//                modifier = Modifier
//                    .clip(RoundedCornerShape(12.dp))
//                    .defaultMinSize(
//                        minWidth = 80.dp,
//                        minHeight = 70.dp
//                    )
//                    .padding(top = 10.dp, bottom = 15.dp),
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Icon(
//                    imageVector = Icons.Outlined.FavoriteBorder,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .height(32.dp)
//                )
//                Spacer(modifier = Modifier.size(4.dp))
//                Text(
//                    text = "Favorite",
//                    modifier = Modifier,
//                    textAlign = TextAlign.Center,
//                    fontFamily = GlobalFont
//                )
//            }
//            Column(
//                modifier = Modifier
//                    .clip(RoundedCornerShape(12.dp))
//                    .defaultMinSize(
//                        minWidth = 80.dp,
//                        minHeight = 70.dp
//                    )
//                    .padding(top = 10.dp, bottom = 15.dp),
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Icon(
//                    imageVector = Icons.Outlined.Delete,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .height(32.dp)
//                )
//                Spacer(modifier = Modifier.size(4.dp))
//                Text(
//                    text = "Delete",
//                    modifier = Modifier,
//                    textAlign = TextAlign.Center,
//                    fontFamily = GlobalFont
//                )
//            }
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
                    Text(
                        text = textCutter(music.mediaMetadata.title.toString(), 20),
                        fontFamily = GlobalFont
                    )
                    Text(
                        text = textCutter(music.mediaMetadata.artist.toString(), 20),
                        fontFamily = GlobalFont,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.85f)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
//                IconButton(
//                    onClick = {
//                        onDismiss()
//                        onNavigate(Screen.MetadataEditor(music.mediaId))
//                    }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.FavoriteBorder,
//                        contentDescription = null
//                    )
//                }
//                IconButton(
//                    onClick = {
//                        createFavoriteRequest(
//                            Uri.parse(music.mediaMetadata.extras?.getString("uri")),
//                            favSongLauncher,
//                            context,
//                            music.mediaMetadata.extras?.getInt("isFavorite") == 0
//                        )
//                    }) {
//                    Icon(
//                        imageVector = Icons.Default.FavoriteBorder,
//                        contentDescription = null
//                    )
//                }
                IconButton(
                    onClick = {
                            createDeleteRequest(
                                Uri.parse(music.mediaMetadata.extras?.getString("uri")),
                                deleteSongLauncher,
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
                    text = "${stringResource(id = R.string.size)}: ${music.mediaMetadata.extras?.getLong("size")?.formatBinarySize()}",
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
//                Text(
//                    text = "${"IS_FAV"}: ${music.mediaMetadata.extras?.getInt("isFavorite")}",
//                    fontFamily = GlobalFont,
//                    modifier = Modifier.padding(bottom = 5.dp)
//                )
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

private fun createFavoriteRequest(
    uri: Uri,
    intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>,
    context: Context,
    value: Boolean
) {

    val coroutineScope = CoroutineScope(Dispatchers.Main)

    coroutineScope.launch {
        try {
            // How do u favorite below A11 ??
        } catch (e: SecurityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val intentSender = MediaStore.createFavoriteRequest(
                    context.contentResolver,
                    listOf(uri),
                    value
                ).intentSender

                val senderRequest: IntentSenderRequest = IntentSenderRequest.Builder(intentSender)
                    .setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION, 0)
                    .build()
                intentSenderLauncher.launch(senderRequest)

            }
        } catch (e: Exception) {
            Log.e(
                ContentValues.TAG,
                "Error trying to favorite song: ${e.message} ${e.stackTrace.joinToString()}"
            )
        }
    }
}
