@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.shared_components

import android.text.format.Formatter
import android.webkit.MimeTypeMap
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.formatToReadableTime
import com.sosauce.cutemusic.utils.getBitrate

@Composable
fun MusicDetailsDialog(
    track: CuteTrack,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDismissRequest,
                shapes = ButtonDefaults.shapes()
            ) {
                Text(
                    text = stringResource(R.string.okay),
                    color = LocalContentColor.current
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.details)
            )
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.info_rounded),
                contentDescription = null
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
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
                                track.artUri,
                                context
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
                                text = track.title,
                                modifier = Modifier
                                    .basicMarquee()
                            )
                            Text(
                                text = track.artist,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.basicMarquee()
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    if (track.isSaf) {
                        SuggestionChip(
                            onClick = {},
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                labelColor = contentColorFor(MaterialTheme.colorScheme.primary)
                            ),
                            label = { Text("S.A.F") }
                        )
                    }
                    SuggestionChip(
                        onClick = {},
                        label = {
                            Text(
                                text = Formatter.formatFileSize(
                                    context,
                                    track.size
                                )
                            )
                        }
                    )
                    SuggestionChip(
                        onClick = {},
                        label = { Text("${track.uri.getBitrate(context)} kbps") }
                    )
                    SuggestionChip(
                        onClick = {},
                        label = {
                            Text(
                                MimeTypeMap.getSingleton()
                                    .getExtensionFromMimeType(context.contentResolver.getType(track.uri))
                                    ?: "No type"
                            )
                        }
                    )
                    SuggestionChip(
                        onClick = {},
                        label = { Text(track.durationMs.formatToReadableTime()) }
                    )
                }
            }
        }
    )
}