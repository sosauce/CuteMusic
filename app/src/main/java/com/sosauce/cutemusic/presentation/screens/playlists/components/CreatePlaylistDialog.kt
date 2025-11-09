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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.models.Playlist
import com.sosauce.cutemusic.domain.actions.PlaylistActions
import com.sosauce.cutemusic.presentation.screens.playlists.PlaylistViewModel
import com.sosauce.cutemusic.utils.copyMutate
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreatePlaylistDialog(
    onDismissRequest: () -> Unit
) {
    val playlistViewModel = koinViewModel<PlaylistViewModel>()
    val playlists by playlistViewModel.allPlaylists.collectAsStateWithLifecycle()
    val localizedPlaylist = stringResource(R.string.playlist)
    var playlist by remember { mutableStateOf(Playlist()) }
    val name = rememberTextFieldState()
    var showEmojiPicker by remember { mutableStateOf(false) }
    var showNewTagDialog by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    LaunchedEffect(name.text) {
        val newName = if (name.text.isEmpty()) {
            "$localizedPlaylist ${playlists.size + 1}"
        } else name.text.toString()
        playlist = playlist.copy(
            name = newName
        )
    }

    if (showNewTagDialog) {
        NewTagDialog(
            onDismissRequest = { showNewTagDialog = false },
            tags = playlist.tags,
            onAddNewTag = { newTag ->
                playlist = playlist.copy(
                    tags = playlist.tags.copyMutate { add(newTag) }
                )
            }
        )
    }

    if (showColorPicker) {
        ColorPickerDialog(
            onDismissRequest = { showColorPicker = false },
            onAddNewColor = { newColor ->
                playlist = playlist.copy(
                    color = newColor
                )
            },
            initialColor = Color(playlist.color)
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
                            playlist = playlist.copy(
                                emoji = it.emoji
                            )
                        })
                    }
                }
            )
        }
    }


    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.create_playlist)) },
        confirmButton = {
            TextButton(
                onClick = {
                    playlistViewModel.handlePlaylistActions(PlaylistActions.CreatePlaylist(playlist))
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.create))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.PlaylistAdd,
                contentDescription = null
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                IconButton(
                    onClick = {
                        playlist = playlist.copy(
                            emoji = ""
                        )
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
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
                    if (playlist.emoji.isNotBlank()) {
                        AnimatedContent(playlist.emoji) {
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
                    placeholder = { Text(playlist.name) }
                )
                Spacer(Modifier.height(20.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text("Color:")
                    if (playlist.color == -1) {
                        Text(
                            text = "Click to add",
                            modifier = Modifier.clickable { showColorPicker = true }
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(20.dp)
                                .background(
                                    color = Color(playlist.color),
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .clickable { showColorPicker = true }
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tags")
                    IconButton(
                        onClick = { showNewTagDialog = true },
                        shapes = IconButtonDefaults.shapes()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null
                        )
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(
                        items = playlist.tags
                    ) { tag ->
                        AssistChip(
                            onClick = {
                                playlist = playlist.copy(
                                    tags = playlist.tags.copyMutate { remove(tag) }
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