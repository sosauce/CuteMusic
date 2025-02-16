@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalSharedTransitionApi::class
)

package com.sosauce.cutemusic.ui.screens.main

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.actions.PlaylistActions
import com.sosauce.cutemusic.data.datastore.rememberGroupByFolders
import com.sosauce.cutemusic.domain.model.Playlist
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.main.components.ShareOptionsContent
import com.sosauce.cutemusic.ui.screens.main.components.SortingDropdownMenu
import com.sosauce.cutemusic.ui.screens.playlists.CreatePlaylistDialog
import com.sosauce.cutemusic.ui.screens.playlists.PlaylistItem
import com.sosauce.cutemusic.ui.shared_components.CuteActionButton
import com.sosauce.cutemusic.ui.shared_components.CuteSearchbar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicDetailsDialog
import com.sosauce.cutemusic.ui.shared_components.PlaylistViewModel
import com.sosauce.cutemusic.utils.ICON_TEXT_SPACING
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.rememberSearchbarAlignment
import org.koin.androidx.compose.koinViewModel
import java.io.File

@Composable
fun SharedTransitionScope.MainScreen(
    musics: List<MediaItem>,
    currentlyPlaying: String,
    isCurrentlyPlaying: Boolean,
    onNavigate: (Screen) -> Unit,
    onShortClick: (String) -> Unit,
    onNavigationItemClicked: (Screen) -> Unit,
    currentScreen: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onLoadMetadata: (String, Uri) -> Unit = { _, _ -> },
    isPlayerReady: Boolean,
    currentMusicUri: String,
    onHandlePlayerAction: (PlayerActions) -> Unit,
    onDeleteMusic: (List<Uri>, ActivityResultLauncher<IntentSenderRequest>) -> Unit,
    onChargeAlbumSongs: (String) -> Unit,
    onChargeArtistLists: (String) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val state = rememberLazyListState()
    var isSortedByASC by remember { mutableStateOf(true) } // I prolly should change this
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var groupByFolders by rememberGroupByFolders()
    val showCuteSearchbar by remember {
        derivedStateOf {
            if (musics.isEmpty()) {
                true
            } else if (
            // Are both the first and last element visible ?
                state.layoutInfo.visibleItemsInfo.firstOrNull()?.index == 0 &&
                state.layoutInfo.visibleItemsInfo.lastOrNull()?.index == musics.size - 1
            ) {
                true
            } else {
                state.layoutInfo.visibleItemsInfo.lastOrNull()?.index != musics.size - 1
            }
        }
    }
    val displayMusics by remember(isSortedByASC, musics, query) {
        derivedStateOf {
            if (query.isNotEmpty()) {
                musics.filter {
                    it.mediaMetadata.title?.contains(
                        other = query,
                        ignoreCase = true
                    ) == true
                }
            } else {
                if (isSortedByASC) musics
                else musics.sortedByDescending { it.mediaMetadata.title.toString() }
            }

        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                state = state,
                contentPadding = paddingValues
            ) {
                if (groupByFolders) {
                    displayMusics.groupBy {
                        File(
                            it.mediaMetadata.extras?.getString("folder") ?: ""
                        ).name
                    }
                        .forEach { folderName, allMusics ->
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(0.95f)
                                            .background(
                                                color = MaterialTheme.colorScheme.surfaceContainer,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .align(Alignment.Center)
                                            .padding(vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.folder_rounded),
                                            contentDescription = null,
                                            modifier = Modifier.padding(start = 5.dp)
                                        )
                                        Spacer(Modifier.width(ICON_TEXT_SPACING.dp))
                                        CuteText(folderName)
                                    }
                                }
                            }
                            items(
                                items = allMusics,
                                key = { it.mediaId }
                            ) { music ->
                                Column(
                                    modifier = Modifier
                                        .animateItem()
                                        .padding(
                                            vertical = 2.dp,
                                            horizontal = 4.dp
                                        )
                                ) {
                                    MusicListItem(
                                        onShortClick = { onShortClick(music.mediaId) },
                                        music = music,
                                        onNavigate = { onNavigate(it) },
                                        currentMusicUri = currentMusicUri,
                                        onLoadMetadata = onLoadMetadata,
                                        showBottomSheet = true,
                                        onDeleteMusic = onDeleteMusic,
                                        onChargeAlbumSongs = onChargeAlbumSongs,
                                        onChargeArtistLists = onChargeArtistLists,
                                        isPlayerReady = isPlayerReady
                                    )
                                }
                            }
                        }
                } else {
                    if (displayMusics.isEmpty()) {
                        item {
                            CuteText(
                                text = stringResource(id = R.string.no_musics_found),
                                modifier = Modifier
                                    .padding(paddingValues)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        items(
                            items = displayMusics,
                            key = { it.mediaId }
                        ) { music ->
                            Column(
                                modifier = Modifier
                                    .animateItem()
                                    .padding(
                                        vertical = 2.dp,
                                        horizontal = 4.dp
                                    )
                            ) {
                                MusicListItem(
                                    onShortClick = { onShortClick(music.mediaId) },
                                    music = music,
                                    onNavigate = { onNavigate(it) },
                                    currentMusicUri = currentMusicUri,
                                    onLoadMetadata = onLoadMetadata,
                                    showBottomSheet = true,
                                    onDeleteMusic = onDeleteMusic,
                                    onChargeAlbumSongs = onChargeAlbumSongs,
                                    onChargeArtistLists = onChargeArtistLists,
                                    isPlayerReady = isPlayerReady
                                )
                            }
                        }
                    }
                }
            }

            Crossfade(
                targetState = showCuteSearchbar,
                label = "",
                modifier = Modifier.align(rememberSearchbarAlignment())
            ) { visible ->
                if (visible) {
                    CuteSearchbar(
                        query = query,
                        onQueryChange = { query = it },
                        trailingIcon = {
                            Row {
                                IconButton(
                                    onClick = { sortMenuExpanded = true }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.Sort,
                                        contentDescription = null
                                    )
                                }
                                IconButton(
                                    onClick = { onNavigate(Screen.Settings) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Settings,
                                        contentDescription = null
                                    )
                                }
                                SortingDropdownMenu(
                                    expanded = sortMenuExpanded,
                                    onDismissRequest = { sortMenuExpanded = false },
                                    isSortedByASC = isSortedByASC,
                                    onChangeSorting = { isSortedByASC = it }
                                )
                            }
                        },
                        currentlyPlaying = currentlyPlaying,
                        onHandlePlayerActions = onHandlePlayerAction,
                        isPlaying = isCurrentlyPlaying,
                        animatedVisibilityScope = animatedVisibilityScope,
                        isPlayerReady = isPlayerReady,
                        onNavigate = { onNavigate(Screen.NowPlaying) },
                        onNavigationItemClicked = onNavigationItemClicked,
                        currentScreen = currentScreen,
                        fab = {
                            CuteActionButton(
                                modifier = Modifier.sharedBounds(
                                    sharedContentState = rememberSharedContentState(key = "fab"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                            ) { onHandlePlayerAction(PlayerActions.PlayRandom) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MusicListItem(
    modifier: Modifier = Modifier,
    music: MediaItem,
    onShortClick: (albumName: String) -> Unit,
    onNavigate: (Screen) -> Unit = {},
    currentMusicUri: String,
    onLoadMetadata: (String, Uri) -> Unit = { _, _ -> },
    showBottomSheet: Boolean = false,
    onDeleteMusic: (List<Uri>, ActivityResultLauncher<IntentSenderRequest>) -> Unit = { _, _ -> },
    onChargeAlbumSongs: (String) -> Unit = {},
    onChargeArtistLists: (String) -> Unit = {},
    isPlayerReady: Boolean,
    onDeleteSafTrack: () -> Unit = {},
    showTrackNumber: Boolean = false
) {

    val context = LocalContext.current
    var isDropDownExpanded by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showShareOptions by remember { mutableStateOf(false) }
    val uri = remember { Uri.parse(music.mediaMetadata.extras?.getString("uri")) }
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
                                val playlist = Playlist(
                                    id = playlist.id,
                                    name = playlist.name,
                                    emoji = playlist.emoji,
                                    musics = playlist.musics.toMutableList()
                                        .apply { add(music.mediaId) }
                                )
                                playlistViewModel.handlePlaylistActions(
                                    PlaylistActions.UpsertPlaylist(playlist)
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
                model = ImageUtils.imageRequester(
                    img = music.mediaMetadata.artworkUri,
                    context = context
                ),
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
                    if (music.mediaMetadata.extras?.getBoolean("is_saf") == false) {
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
                        onClick = {
                            if (music.mediaMetadata.extras?.getBoolean("is_saf") == false) {
                                onDeleteMusic(listOf(uri), deleteSongLauncher)
                            } else {
                                onDeleteSafTrack()
                            }
                        },
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



