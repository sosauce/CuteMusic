@file:OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalSharedTransitionApi::class
)

package com.sosauce.cutemusic.ui.shared_components

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlaylistActions
import com.sosauce.cutemusic.domain.model.Playlist
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.main.components.ShareOptionsContent
import com.sosauce.cutemusic.ui.screens.playlists.CreatePlaylistDialog
import com.sosauce.cutemusic.ui.screens.playlists.PlaylistItem
import com.sosauce.cutemusic.utils.CurrentScreen
import com.sosauce.cutemusic.utils.ICON_TEXT_SPACING
import com.sosauce.cutemusic.utils.ImageUtils
import org.koin.androidx.compose.koinViewModel

@Composable
fun SharedTransitionScope.LocalMusicListItem(
    modifier: Modifier = Modifier,
    music: MediaItem,
    onShortClick: (albumName: String) -> Unit,
    onNavigate: (Screen) -> Unit,
    currentMusicUri: String,
    onLoadMetadata: (String, Uri) -> Unit = { _, _ -> },
    onDeleteMusic: (List<Uri>, ActivityResultLauncher<IntentSenderRequest>) -> Unit = { _, _ -> },
    onChargeAlbumSongs: (String) -> Unit = {},
    onChargeArtistLists: (String) -> Unit = {},
    isPlayerReady: Boolean,
    showTrackNumber: Boolean = false,
) {

    val context = LocalContext.current
    var isDropDownExpanded by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showShareOptions by remember { mutableStateOf(false) }
    val uri = remember { music.mediaMetadata.extras?.getString("uri")?.toUri() ?: Uri.EMPTY }
    val path = remember { music.mediaMetadata.extras?.getString("path") ?: "" }
    val isPlaying = currentMusicUri == uri.toString()
    val bgColor by animateColorAsState(
        targetValue = if (isPlaying && isPlayerReady) {
            MaterialTheme.colorScheme.surfaceContainer
        } else {
            Color.Transparent
        },
        label = "Background Color",
        animationSpec = tween(500)
    )
    val materialSurfaceContainer = MaterialTheme.colorScheme.surfaceContainer
    val materialOnSurface = MaterialTheme.colorScheme.onSurface
    val deleteSongLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                Toast.makeText(
                    context,
                    context.resources.getText(R.string.deleting_song_OK),
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                Toast.makeText(
                    context,
                    context.resources.getText(R.string.error_deleting_song),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    var showPlaylistDialog by remember { mutableStateOf(false) }
    var showPlaylistCreatorDialog by remember { mutableStateOf(false) }


    if (showDetailsDialog) {
        MusicDetailsDialog(
            music = music,
            onDismissRequest = { showDetailsDialog = false }
        )
    }

    if (showShareOptions) {
        BasicAlertDialog(
            onDismissRequest = { showShareOptions = false }
        ) { ShareOptionsContent() }
    }

    if (showPlaylistCreatorDialog) {
        CreatePlaylistDialog { showPlaylistCreatorDialog = false }
    }

    if (showPlaylistDialog) {
        val playlistViewModel = koinViewModel<PlaylistViewModel>()
        val playlists by playlistViewModel.allPlaylists.collectAsStateWithLifecycle()

        ModalBottomSheet(
            onDismissRequest = { showPlaylistDialog = false }
        ) {
            LazyColumn {
                item {
                    OutlinedButton(
                        onClick = { showPlaylistCreatorDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(ICON_TEXT_SPACING.dp))
                            CuteText(stringResource(R.string.create_playlist))
                        }
                    }
                }

                items(
                    items = playlists,
                    key = { it.id }
                ) { playlist ->
                    PlaylistItem(
                        playlist = playlist,
                        allowEditAction = false,
                        onClickPlaylist = {
                            if (playlist.musics.contains(music.mediaId)) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.alrdy_in_playlist),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val newPlaylist = Playlist(
                                    id = playlist.id,
                                    name = playlist.name,
                                    emoji = playlist.emoji,
                                    musics = playlist.musics.toMutableList()
                                        .apply { add(music.mediaId) }
                                )
                                playlistViewModel.handlePlaylistActions(
                                    PlaylistActions.UpsertPlaylist(newPlaylist)
                                )
                            }
                        }
                    )
                }
            }
        }

    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .combinedClickable(
                onClick = { onShortClick(music.mediaId) }
            )
            .background(
                color = bgColor,
                shape = RoundedCornerShape(24.dp)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            AsyncImage(
                model = ImageUtils.imageRequester(music.mediaMetadata.artworkUri),
                contentDescription = stringResource(R.string.artwork),
                modifier = Modifier
                    .padding(start = 10.dp)
                    .drawWithContent {
                        drawContent()
                        if (showTrackNumber && music.mediaMetadata.trackNumber != null && music.mediaMetadata.trackNumber != 0) {
                            val circleCenter = Offset(size.width, size.height / 12)
                            drawCircle(
                                color = materialSurfaceContainer,
                                center = circleCenter,
                                radius = 25f
                            )
                            val text = Paint().apply {
                                color = materialOnSurface.toArgb()
                                textSize = 30f
                                textAlign = Paint.Align.CENTER
                            }
                            drawContext.canvas.nativeCanvas.drawText(
                                music.mediaMetadata.trackNumber.toString(),
                                circleCenter.x,
                                circleCenter.y - (text.ascent() + text.descent()) / 2,
                                text
                            )
                        }
                    }
//                    .sharedElement(
//                        state = rememberSharedContentState(key = SharedTransitionKeys.MUSIC_ARTWORK + mediaId),
//                        animatedVisibilityScope = animatedVisibilityScope
//                    )
                    .size(45.dp)
                    .clip(RoundedCornerShape(5.dp)),
                contentScale = ContentScale.Crop,
            )

            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                CuteText(
                    text = music.mediaMetadata.title.toString(),
                    maxLines = 1,
                    modifier = Modifier.basicMarquee()
                )
                CuteText(
                    text = music.mediaMetadata.artist.toString(),
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.85f)
                )
            }
        }
        Row {
            IconButton(
                onClick = { isDropDownExpanded = true }
            ) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = null
                )
            }
            DropdownMenu(
                expanded = isDropDownExpanded,
                onDismissRequest = { isDropDownExpanded = false },
                shape = RoundedCornerShape(24.dp)
            ) {
                DropdownMenuItem(
                    onClick = { showDetailsDialog = true },
                    text = {
                        CuteText(stringResource(R.string.details))
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.info_rounded),
                            contentDescription = null
                        )
                    }
                )
                DropdownMenuItem(
                    onClick = {
                        isDropDownExpanded = false
                        onLoadMetadata(path, uri)
                        onNavigate(Screen.MetadataEditor(music.mediaId))
                    },
                    text = {
                        CuteText(stringResource(R.string.edit))
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.edit_rounded),
                            contentDescription = null
                        )
                    }
                )
                if (CurrentScreen.screen != Screen.AlbumsDetails.toString()) {
                    DropdownMenuItem(
                        onClick = {
                            isDropDownExpanded = false
                            onChargeAlbumSongs(music.mediaMetadata.albumTitle.toString())
                            onNavigate(
                                Screen.AlbumsDetails(
                                    music.mediaMetadata.extras?.getLong("album_id") ?: 0
                                )
                            )
                        },
                        text = {
                            CuteText("${stringResource(R.string.go_to)} ${music.mediaMetadata.albumTitle}")
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(androidx.media3.session.R.drawable.media3_icon_album),
                                contentDescription = null
                            )
                        }
                    )
                }
                if (CurrentScreen.screen != Screen.ArtistsDetails.toString()) {
                    DropdownMenuItem(
                        onClick = {
                            isDropDownExpanded = false
                            onChargeArtistLists(music.mediaMetadata.artist.toString())
                            onNavigate(
                                Screen.ArtistsDetails(
                                    music.mediaMetadata.extras?.getLong("artist_id") ?: 0
                                )
                            )
                        },
                        text = {
                            CuteText("${stringResource(R.string.go_to)} ${music.mediaMetadata.artist}")
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.artist_rounded),
                                contentDescription = null
                            )
                        }
                    )
                }
                DropdownMenuItem(
                    onClick = { showPlaylistDialog = true },
                    text = {
                        CuteText(stringResource(R.string.add_to_playlist))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.PlaylistAdd,
                            contentDescription = null
                        )
                    }
                )
                DropdownMenuItem(
                    onClick = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            type = "audio/*"
                        }

                        context.startActivity(
                            Intent.createChooser(
                                shareIntent,
                                null
                            )
                        )
                    },
                    text = {
                        CuteText(
                            text = stringResource(R.string.share)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(androidx.media3.session.R.drawable.media3_icon_share),
                            contentDescription = null
                        )
                    }
                )
                DropdownMenuItem(
                    onClick = { onDeleteMusic(listOf(uri), deleteSongLauncher) },
                    text = {
                        CuteText(
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
}

@Composable
fun SafMusicListItem(
    modifier: Modifier = Modifier,
    music: MediaItem,
    onShortClick: (albumName: String) -> Unit,
    currentMusicUri: String,
    showBottomSheet: Boolean = false,
    onDeleteFromSaf: () -> Unit,
    isPlayerReady: Boolean,
    showTrackNumber: Boolean = false
) {

    val context = LocalContext.current
    var isDropDownExpanded by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showShareOptions by remember { mutableStateOf(false) }
    val uri = remember { music.mediaMetadata.extras?.getString("uri")?.toUri() ?: Uri.EMPTY }
    val isPlaying = currentMusicUri == uri.toString()
    val bgColor by animateColorAsState(
        targetValue = if (isPlaying && isPlayerReady) {
            MaterialTheme.colorScheme.surfaceContainer
        } else {
            Color.Transparent
        },
        label = "Background Color",
        animationSpec = tween(500)
    )
    val materialSurfaceContainer = MaterialTheme.colorScheme.surfaceContainer
    val materialOnSurface = MaterialTheme.colorScheme.onSurface
    var showPlaylistDialog by remember { mutableStateOf(false) }
    var showPlaylistCreatorDialog by remember { mutableStateOf(false) }


    if (showDetailsDialog) {
        MusicDetailsDialog(
            music = music,
            onDismissRequest = { showDetailsDialog = false }
        )
    }

    if (showShareOptions) {
        BasicAlertDialog(
            onDismissRequest = { showShareOptions = false }
        ) { ShareOptionsContent() }
    }

    if (showPlaylistCreatorDialog) {
        CreatePlaylistDialog { showPlaylistCreatorDialog = false }
    }

    if (showPlaylistDialog) {
        val playlistViewModel = koinViewModel<PlaylistViewModel>()
        val playlists by playlistViewModel.allPlaylists.collectAsStateWithLifecycle()

        ModalBottomSheet(
            onDismissRequest = { showPlaylistDialog = false }
        ) {
            LazyColumn {
                item {
                    OutlinedButton(
                        onClick = { showPlaylistCreatorDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(ICON_TEXT_SPACING.dp))
                            CuteText(stringResource(R.string.create_playlist))
                        }
                    }
                }

                items(
                    items = playlists,
                    key = { it.id }
                ) { playlist ->
                    PlaylistItem(
                        playlist = playlist,
                        allowEditAction = false,
                        onClickPlaylist = {
                            if (playlist.musics.contains(music.mediaId)) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.alrdy_in_playlist),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val newPlaylist = Playlist(
                                    id = playlist.id,
                                    name = playlist.name,
                                    emoji = playlist.emoji,
                                    musics = playlist.musics.toMutableList()
                                        .apply { add(music.mediaId) }
                                )
                                playlistViewModel.handlePlaylistActions(
                                    PlaylistActions.UpsertPlaylist(newPlaylist)
                                )
                            }
                        }
                    )
                }
            }
        }

    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .combinedClickable(
                onClick = { onShortClick(music.mediaId) }
            )
            .background(
                color = bgColor,
                shape = RoundedCornerShape(24.dp)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            AsyncImage(
                model = ImageUtils.imageRequester(music.mediaMetadata.artworkUri),
                stringResource(R.string.artwork),
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(45.dp)
                    .drawWithContent {
                        drawContent()
                        if (showTrackNumber && music.mediaMetadata.trackNumber != null && music.mediaMetadata.trackNumber != 0) {
                            val circleCenter = Offset(size.width, size.height / 12)
                            drawCircle(
                                color = materialSurfaceContainer,
                                center = circleCenter,
                                radius = 25f
                            )
                            val text = Paint().apply {
                                color = materialOnSurface.toArgb()
                                textSize = 30f
                                textAlign = Paint.Align.CENTER
                            }
                            drawContext.canvas.nativeCanvas.drawText(
                                music.mediaMetadata.trackNumber.toString(),
                                circleCenter.x,
                                circleCenter.y - (text.ascent() + text.descent()) / 2,
                                text
                            )
                        }
                    }
                    .clip(RoundedCornerShape(5.dp)),
                contentScale = ContentScale.Crop,
            )

            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                CuteText(
                    text = music.mediaMetadata.title.toString(),
                    maxLines = 1,
                    modifier = Modifier.basicMarquee()
                )
                CuteText(
                    text = music.mediaMetadata.artist.toString(),
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.85f)
                )
            }
        }

        if (showBottomSheet) {
            Row {
                IconButton(
                    onClick = { isDropDownExpanded = true }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = null
                    )
                }
                DropdownMenu(
                    expanded = isDropDownExpanded,
                    onDismissRequest = { isDropDownExpanded = false },
                    shape = RoundedCornerShape(24.dp)
                ) {
                    DropdownMenuItem(
                        onClick = { showDetailsDialog = true },
                        text = {
                            CuteText(stringResource(R.string.details))
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.info_rounded),
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        onClick = { showPlaylistDialog = true },
                        text = {
                            CuteText(stringResource(R.string.add_to_playlist))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.PlaylistAdd,
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                type = "audio/*"
                            }

                            context.startActivity(
                                Intent.createChooser(
                                    shareIntent,
                                    null
                                )
                            )
                        },
                        text = {
                            CuteText(
                                text = stringResource(R.string.share)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(androidx.media3.session.R.drawable.media3_icon_share),
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        onClick = onDeleteFromSaf,
                        text = {
                            CuteText(
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
    }
}