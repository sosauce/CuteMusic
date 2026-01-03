@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.playlists.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji2.emojipicker.EmojiPickerView
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.models.Playlist
import com.sosauce.cutemusic.domain.actions.PlaylistActions
import com.sosauce.cutemusic.utils.copyMutate


@Composable
fun EditPlaylist(
    playlist: Playlist,
    onDismissRequest: () -> Unit,
    onHandlePlaylistActions: (PlaylistActions) -> Unit,
) {

    var newPlaylist by remember { mutableStateOf(playlist) }
    val name = rememberTextFieldState(initialText = newPlaylist.name)
    var showEmojiPicker by remember { mutableStateOf(false) }
    var showNewTagDialog by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    LaunchedEffect(name.text) {
        val newName = if (name.text.isEmpty()) {
            playlist.name
        } else name.text.toString()
        newPlaylist = newPlaylist.copy(
            name = newName
        )
    }

    if (showNewTagDialog) {
        NewTagDialog(
            onDismissRequest = { showNewTagDialog = false },
            tags = newPlaylist.tags,
            onAddNewTag = { newTag ->
                newPlaylist = newPlaylist.copy(
                    tags = newPlaylist.tags.copyMutate { add(newTag) }
                )
            }
        )
    }

    if (showColorPicker) {
        ColorPickerDialog(
            onDismissRequest = { showColorPicker = false },
            onAddNewColor = { newColor ->
                newPlaylist = newPlaylist.copy(
                    color = newColor
                )
            },
            initialColor = Color(newPlaylist.color)
        )
    }


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
                            newPlaylist = newPlaylist.copy(emoji = it.emoji)
                        })
                    }
                }
            )
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.modify_playlist)) },
        confirmButton = {
            TextButton(
                onClick = {
                    onHandlePlaylistActions(
                        PlaylistActions.UpsertPlaylist(newPlaylist)
                    )
                    onDismissRequest()
                },
                enabled = newPlaylist != playlist
            ) {
                Text(stringResource(R.string.modify))
            }
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.edit_rounded),
                contentDescription = null
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                IconButton(
                    onClick = {
                        newPlaylist = newPlaylist.copy(
                            emoji = ""
                        )
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.close),
                        contentDescription = stringResource(R.string.remove_emoji),
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(100.dp)
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(10))
                        .clickable {
                            showEmojiPicker = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (newPlaylist.emoji.isNotBlank()) {
                        AnimatedContent(newPlaylist.emoji) {
                            Text(
                                text = it,
                                fontSize = 40.sp
                            )
                        }
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.add_emoji_rounded),
                            contentDescription = stringResource(R.string.emoji),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                OutlinedTextField(
                    state = name,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    placeholder = { Text(newPlaylist.name) }
                )
                Spacer(Modifier.height(20.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text("${stringResource(R.string.color)}:")
                    if (newPlaylist.color == -1) {
                        Text(
                            text = stringResource(R.string.click_to_add),
                            modifier = Modifier.clickable { showColorPicker = true }
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(20.dp)
                                .background(
                                    color = Color(newPlaylist.color),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable { showColorPicker = true }
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.tags))
                    IconButton(
                        onClick = { showNewTagDialog = true },
                        shapes = IconButtonDefaults.shapes()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.add),
                            contentDescription = null
                        )
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(
                        items = newPlaylist.tags
                    ) { tag ->
                        AssistChip(
                            onClick = {
                                newPlaylist = newPlaylist.copy(
                                    tags = newPlaylist.tags.copyMutate { remove(tag) }
                                )
                            },
                            label = { Text(tag) }
                        )
                    }
                }
            }
        }
    )
}