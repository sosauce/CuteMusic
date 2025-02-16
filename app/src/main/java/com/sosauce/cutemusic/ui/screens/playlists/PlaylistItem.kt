@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.ui.screens.playlists

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji2.emojipicker.EmojiPickerView
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.domain.model.Playlist
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.utils.ICON_TEXT_SPACING

@Composable
fun PlaylistItem(
    modifier: Modifier = Modifier,
    playlist: Playlist,
    onDeletePlaylist: () -> Unit = {},
    onUpsertPlaylist: (Playlist) -> Unit = {},
    allowEditAction: Boolean = true,
    onClickPlaylist: () -> Unit = {}
) {

    var showEditDialog by remember { mutableStateOf(false) }

    if (showEditDialog) {
        EditPlaylist(
            playlist = playlist,
            onDismissRequest = { showEditDialog = false },
            onDeletePlaylist = onDeletePlaylist,
            onUpsertPlaylist = onUpsertPlaylist
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .combinedClickable(
                onClick = onClickPlaylist
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(45.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer.copy(0.5f)),
                contentAlignment = Alignment.Center
            ) {
                if (playlist.emoji.isNotBlank()) {
                    CuteText(
                        text = playlist.emoji,
                        fontSize = 20.sp
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                CuteText(
                    text = playlist.name,
                    maxLines = 1,
                    modifier = Modifier.basicMarquee()
                )
                CuteText(
                    text = "${playlist.musics.size} songs",
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.85f)
                )
            }
        }
        if (allowEditAction) {
            Row {
                IconButton(
                    onClick = { showEditDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
private fun EditPlaylist(
    playlist: Playlist,
    onDismissRequest: () -> Unit,
    onDeletePlaylist: () -> Unit,
    onUpsertPlaylist: (Playlist) -> Unit
) {

    var name by remember { mutableStateOf(playlist.name) }
    var emoji by remember { mutableStateOf(playlist.emoji) }
    var showEmojiPicker by remember { mutableStateOf(false) }


    if (showEmojiPicker) {
        ModalBottomSheet(
            onDismissRequest = { showEmojiPicker = false },
            dragHandle = null
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                factory = { ctx ->
                    EmojiPickerView(ctx).apply {
                        setOnEmojiPickedListener(onEmojiPickedListener = {
                            emoji = it.emoji
                        })
                    }
                }
            )
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { CuteText(stringResource(R.string.modify_playlist)) },
        confirmButton = {
            TextButton(
                onClick = {
                    val playlist = Playlist(
                        id = playlist.id,
                        name = name,
                        emoji = emoji,
                        musics = playlist.musics
                    )
                    onUpsertPlaylist(playlist)
                    onDismissRequest()
                }
            ) {
                CuteText(stringResource(R.string.modify))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                CuteText(stringResource(R.string.cancel))
            }
        },
        text = {
            Column {
                IconButton(
                    onClick = { emoji = "" },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(100.dp)
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(5))
                        .clickable { showEmojiPicker = true },
                    contentAlignment = Alignment.Center
                ) {
                    Crossfade(
                        targetState = emoji.isNotBlank()
                    ) { hasEmoji ->
                        if (hasEmoji) {
                            AnimatedContent(emoji) {
                                CuteText(
                                    text = it,
                                    fontSize = 40.sp
                                )
                            }
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.add_emoji_rounded),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
                )
                Spacer(Modifier.height(25.dp))
                OutlinedButton(
                    onClick = onDeletePlaylist,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.trash_rounded),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.width(ICON_TEXT_SPACING.dp))
                        CuteText(
                            text = stringResource(R.string.del_playlist),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    )
}