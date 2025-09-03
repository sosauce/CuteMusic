@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.presentation.screens.playlists.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
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
import com.sosauce.cutemusic.data.actions.PlaylistActions
import com.sosauce.cutemusic.domain.model.Playlist
import com.sosauce.cutemusic.presentation.shared_components.CuteText

@Composable
fun EditPlaylist(
    playlist: Playlist,
    onDismissRequest: () -> Unit,
    onHandlePlaylistActions: (PlaylistActions) -> Unit,
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
                    val newPlaylist = Playlist(
                        id = playlist.id,
                        name = name,
                        emoji = emoji,
                        musics = playlist.musics
                    )
                    onHandlePlaylistActions(
                        PlaylistActions.UpsertPlaylist(newPlaylist)
                    )
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
                    if (emoji.isNotBlank()) {
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
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    )
                )
            }
        }
    )
}