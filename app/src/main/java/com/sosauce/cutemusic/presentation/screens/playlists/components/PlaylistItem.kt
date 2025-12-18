@file:OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.cutemusic.presentation.screens.playlists.components

import android.view.MenuItem
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.util.fastForEachIndexed
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.models.Playlist
import com.sosauce.cutemusic.domain.actions.PlaylistActions
import com.sosauce.cutemusic.presentation.shared_components.DeletionDialog
import com.sosauce.cutemusic.presentation.shared_components.MoreOptions
import com.sosauce.cutemusic.presentation.shared_components.PlaylistDeletionDialog

@Composable
fun PlaylistItem(
    modifier: Modifier = Modifier,
    playlist: Playlist,
    onHandlePlaylistActions: (PlaylistActions) -> Unit,
    onClickPlaylist: () -> Unit,
    enabled: Boolean = true
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
    val playlistOptions = listOf(
        MoreOptions(
            text = { stringResource(R.string.edit_playlist) },
            onClick = { showEditDialog = true },
            icon = R.drawable.edit_rounded
        ),
        MoreOptions(
            text = { stringResource(R.string.export_playlist) },
            onClick = { exportPlaylistLauncher.launch("${playlist.name.ifEmpty { "Playlist" }}.m3u") },
            icon = R.drawable.export
        ),
        MoreOptions(
            text = { stringResource(R.string.delete) },
            onClick = { showDeletionDialog = true },
            icon = R.drawable.trash_rounded,
            tint = MaterialTheme.colorScheme.error
        )
    )

    if (showEditDialog) {
        EditPlaylist(
            playlist = playlist,
            onDismissRequest = { showEditDialog = false },
            onHandlePlaylistActions = onHandlePlaylistActions
        )
    }

    if (showDeletionDialog) {
        PlaylistDeletionDialog(
            playlist = playlist,
            onHandlePlaylistAction = onHandlePlaylistActions,
            onDismissRequest = { showDeletionDialog = false }
        )
    }

    Surface(
        modifier = modifier
            .padding(3.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                enabled = enabled,
                onClick = onClickPlaylist
            ),
        color = Color.Transparent
    ) {
        CompositionLocalProvider(LocalContentColor provides if (enabled) LocalContentColor.current else MaterialTheme.colorScheme.onSurface.copy(0.38f)) {
            Row(
                modifier = modifier
                    .padding(vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .padding(start = 10.dp)
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
                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = playlist.name,
                        maxLines = 1,
                        modifier = Modifier.basicMarquee()
                    )

                    val bottomText = if (enabled) {
                        pluralStringResource(
                            R.plurals.tracks,
                            playlist.musics.size,
                            playlist.musics.size
                        )
                    } else {
                        stringResource(R.string.already_in_playlist)
                    }

                    Text(
                        text = bottomText,
                        maxLines = 1,
                        color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface.copy(0.38f)
                    )
                }
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

                    DropdownMenuPopup(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        DropdownMenuGroup(
                            shapes = MenuDefaults.groupShapes()
                        ) {
                            playlistOptions.fastForEachIndexed { index, option ->
                                DropdownMenuItem(
                                    onClick = option.onClick,
                                    shape = when (index) {
                                        0 -> MenuDefaults.leadingItemShape
                                        playlistOptions.lastIndex -> MenuDefaults.trailingItemShape
                                        else -> MenuDefaults.middleItemShape
                                    },
                                    text = {
                                        Text(
                                            text = option.text(),
                                            color = option.tint ?: LocalContentColor.current
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(option.icon),
                                            contentDescription = null,
                                            tint = option.tint ?: LocalContentColor.current
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}
