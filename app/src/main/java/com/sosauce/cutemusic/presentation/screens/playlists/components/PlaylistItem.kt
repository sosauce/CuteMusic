@file:OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.cutemusic.presentation.screens.playlists.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.models.Playlist
import com.sosauce.cutemusic.domain.actions.PlaylistActions
import com.sosauce.cutemusic.presentation.shared_components.CuteDropdownMenuItem
import com.sosauce.cutemusic.presentation.shared_components.DeletionDialog

@Composable
fun PlaylistItem(
    modifier: Modifier = Modifier,
    playlist: Playlist,
    onHandlePlaylistActions: (PlaylistActions) -> Unit,
    onClickPlaylist: () -> Unit
) {

    var isDropdownExpanded by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeletionDialog by remember { mutableStateOf(false) }

    val exportPlaylistLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("*/*")) { uri ->
            uri?.let {
                onHandlePlaylistActions(
                    PlaylistActions.ExportM3uPlaylist(
                        uri = it,
                        tracks = playlist.musics
                    )
                )
            }

        }

    if (showEditDialog) {
        EditPlaylist(
            playlist = playlist,
            onDismissRequest = { showEditDialog = false },
            onHandlePlaylistActions = onHandlePlaylistActions
        )
    }

    if (showDeletionDialog) {
        DeletionDialog(
            onDismissRequest = { showDeletionDialog = false },
            onDelete = {
                onHandlePlaylistActions(
                    PlaylistActions.DeletePlaylist(playlist)
                )
            }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (playlist.emoji.isNotEmpty()) {
                    Text(
                        text = playlist.emoji,
                        fontSize = 50.sp
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.playlist),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )
                }
                Text(
                    text = playlist.name,
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    modifier = Modifier.basicMarquee()
                )
            }
        }
    }

    DropdownMenuItem(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        onClick = onClickPlaylist,
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(RoundedCornerShape(5.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (playlist.emoji.isNotBlank()) {
                    Text(
                        text = playlist.emoji,
                        fontSize = 20.sp
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.queue_music_rounded),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 15.dp)
            ) {
                Text(
                    text = playlist.name,
                    maxLines = 1,
                    modifier = Modifier.basicMarquee()
                )
                Text(
                    text = pluralStringResource(
                        R.plurals.tracks,
                        playlist.musics.size,
                        playlist.musics.size
                    ),
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.85f)
                )
            }
        },
        trailingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (playlist.color != -1) {
                    Box(
                        modifier = Modifier
                            .size(15.dp)
                            .background(
                                color = Color(playlist.color),
                                shape = MaterialShapes.Circle.toShape()
                            )
                    )
                }
                IconButton(
                    onClick = { isDropdownExpanded = true }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.more_vert),
                        contentDescription = null
                    )
                }

                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false },
                    shape = RoundedCornerShape(24.dp)
                ) {
                    CuteDropdownMenuItem(
                        onClick = { showEditDialog = true },
                        text = { Text(stringResource(R.string.edit_playlist)) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.edit_rounded),
                                contentDescription = null
                            )
                        }
                    )
                    CuteDropdownMenuItem(
                        onClick = { exportPlaylistLauncher.launch("${playlist.name.ifEmpty { "Playlist" }}.m3u") },
                        text = { Text(stringResource(R.string.export_playlist)) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.export),
                                contentDescription = null
                            )
                        }
                    )
                    CuteDropdownMenuItem(
                        onClick = { showDeletionDialog = true },
                        text = {
                            Text(
                                text = stringResource(R.string.delete),
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.trash_rounded),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    )
                }
            }
        }
    )
}
