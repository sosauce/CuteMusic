@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.ui.screens.playlists

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji2.emojipicker.EmojiPickerView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlaylistActions
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.PlaylistViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreatePlaylistDialog(
    onDismissRequest: () -> Unit
) {
    val playlistViewModel = koinViewModel<PlaylistViewModel>()
    val playlists by playlistViewModel.allPlaylists.collectAsStateWithLifecycle()
    val playlistState by playlistViewModel.state.collectAsStateWithLifecycle()
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
                            playlistViewModel.handlePlaylistActions(PlaylistActions.UpdateStateEmoji(it.emoji))
                        })
                    }
                }
            )
        }
    }


    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { CuteText(stringResource(R.string.create_playlist)) },
        confirmButton = {
            TextButton(
                onClick = {
                    playlistViewModel.handlePlaylistActions(PlaylistActions.CreatePlaylist)
                    onDismissRequest()
                }
            ) {
                CuteText(stringResource(R.string.create))
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
                    onClick = { playlistViewModel.handlePlaylistActions(PlaylistActions.UpdateStateEmoji("")) },
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
                    if (playlistState.emoji.isNotBlank()) {
                        AnimatedContent(playlistState.emoji) {
                            CuteText(
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
                    value = playlistState.name,
                    onValueChange = { playlistViewModel.handlePlaylistActions(PlaylistActions.UpdateStateName(it)) },
                    placeholder = {
                        CuteText("${stringResource(R.string.playlist)} ${playlists.size + 1}")
                    }
                )
            }
        }
    )
}