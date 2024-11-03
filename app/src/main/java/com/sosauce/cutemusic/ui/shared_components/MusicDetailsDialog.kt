package com.sosauce.cutemusic.ui.shared_components

import android.net.Uri
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.MusicState
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.formatBinarySize
import com.sosauce.cutemusic.utils.formatToReadableTime
import com.sosauce.cutemusic.utils.getBitrate

@UnstableApi
@Composable
fun MusicDetailsDialog(
    music: MediaItem,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val uri = remember { Uri.parse(music.mediaMetadata.extras?.getString("uri")) }
    val fileBitrate = uri.getBitrate(context)
    val fileType =
        context.contentResolver.getType(uri)

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                CuteText(stringResource(R.string.okay))
            }
        },
        title = {
            CuteText(stringResource(R.string.details))
        },
        text = {
            Column {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        MaterialTheme.colorScheme.surfaceContainerHighest
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
                Spacer(Modifier.height(10.dp))
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
                CuteText(
                    text = "${stringResource(id = R.string.duration)}: ${music.mediaMetadata.durationMs?.formatToReadableTime() ?: 0}",
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }
        }
    )
}

@Composable
fun MusicStateDetailsDialog(
    musicState: MusicState,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val uri = remember { Uri.parse(musicState.currentMusicUri) }
    val fileBitrate = uri.getBitrate(context)
    val fileType =
        context.contentResolver.getType(uri)

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                CuteText(stringResource(R.string.okay))
            }
        },
        title = {
            CuteText(stringResource(R.string.details))
        },
        text = {
            Column {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = ImageUtils.imageRequester(
                                img = musicState.currentArt,
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
                                text = musicState.currentlyPlaying,
                                modifier = Modifier
                                    .basicMarquee()
                            )
                            CuteText(
                                text = musicState.currentArtist,
                                color = MaterialTheme.colorScheme.onBackground.copy(0.85f),
                                modifier = Modifier.basicMarquee()
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                CuteText(
                    text = "${stringResource(id = R.string.size)}: ${
                        musicState.currentSize.formatBinarySize()
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
    )
}