@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.playlists.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.toShape
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.emoji2.emojipicker.EmojiPickerView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sosauce.chocola.R
import com.sosauce.chocola.data.models.Playlist
import com.sosauce.chocola.domain.actions.MetadataActions
import com.sosauce.chocola.domain.actions.PlaylistActions
import com.sosauce.chocola.presentation.screens.playlists.PlaylistViewModel
import com.sosauce.chocola.presentation.shared_components.EmojiPicker
import com.sosauce.chocola.utils.copyMutate
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreatePlaylistDialog(
    onDismissRequest: () -> Unit
) {
    val playlistViewModel = koinViewModel<PlaylistViewModel>()
    val state by playlistViewModel.state.collectAsStateWithLifecycle()
    val localizedPlaylist = stringResource(R.string.playlist)
    var playlist by remember { mutableStateOf(Playlist()) }
    val name = rememberTextFieldState()
    var showEmojiPicker by remember { mutableStateOf(false) }
    var showNewTagDialog by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    LaunchedEffect(name.text) {
        val newName = if (name.text.isEmpty()) {
            "$localizedPlaylist ${state.playlists.size + 1}"
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
        Dialog(
            onDismissRequest = { showEmojiPicker = false },
            properties = DialogProperties(dismissOnBackPress = true, usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
        ) {
            EmojiPicker(
                onEmojiPicked = { playlist = playlist.copy(emoji = it) },
                onDismiss = { showEmojiPicker = false }
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
                },
                shapes = ButtonDefaults.shapes()
            ) {
                Text(stringResource(R.string.create))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                shapes = ButtonDefaults.shapes()
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.playlist_add),
                contentDescription = null
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {

                Text(
                    text = stringResource(R.string.name_and_emoji),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 4.dp)
                )
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHighest)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                .clickable { showEmojiPicker = true },
                            contentAlignment = Alignment.Center
                        ) {
                            if (playlist.emoji.isNotBlank()) {
                                AnimatedContent(playlist.emoji) {
                                    Text(
                                        text = it,
                                        fontSize = 35.sp
                                    )
                                }
                            } else {
                                Icon(
                                    painter = painterResource(R.drawable.add_emoji_rounded),
                                    contentDescription = stringResource(R.string.emoji),
                                    modifier = Modifier.size(35.dp)
                                )
                            }
                        }
                        OutlinedTextField(
                            state = name,
                            lineLimits = TextFieldLineLimits.SingleLine,
                            shape = RoundedCornerShape(12.dp),
                            placeholder = { Text(playlist.name) }
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.color),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 4.dp)
                )
                val colorCard = if (playlist.color != -1) Color(playlist.color) else MaterialTheme.colorScheme.surfaceContainerHighest

                Card(
                    onClick = { showColorPicker = true },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorCard,
                        contentColor = if (colorCard.luminance() > 0.5f) Color.Black else Color.White
                    )
                ) {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        val icon = if (playlist.color != -1) R.drawable.edit_filled else R.drawable.add

                        Icon(
                            painter = painterResource(icon),
                            contentDescription = null,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.tags),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 4.dp)
                )
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHighest)
                    )
                ) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        contentPadding = PaddingValues(10.dp)
                    ) {
                        item(
                            key = "Add tag"
                        ) {
                            Button(
                                modifier = Modifier.animateItem(),
                                onClick = { showNewTagDialog = true },
                                shapes = ButtonDefaults.shapes(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHigh)
                                )
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.add),
                                    contentDescription = null
                                )
                            }
                        }
                        items(
                            items = playlist.tags
                        ) { tag ->
                            Button(
                                modifier = Modifier.animateItem(),
                                onClick = {
                                    playlist = playlist.copy(
                                        tags = playlist.tags.copyMutate { remove(tag) }
                                    )
                                },
                                shapes = ButtonDefaults.shapes(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHigh)
                                )
                            ) { Text(tag) }
                        }
                    }
                }

            }
        }
    )
}