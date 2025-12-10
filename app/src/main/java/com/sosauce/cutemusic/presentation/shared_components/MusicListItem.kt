@file:OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.cutemusic.presentation.shared_components

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ShareCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.presentation.navigation.Screen
import com.sosauce.cutemusic.presentation.screens.playlists.components.PlaylistPicker
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.LocalScreen

@Composable
fun LocalMusicListItem(
    modifier: Modifier = Modifier,
    music: CuteTrack,
    musicState: MusicState,
    onShortClick: (mediaId: String) -> Unit,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    playlistDropdownMenuItem: @Composable () -> Unit = { AddToPlaylistDropdownItem(music) },
    trailingContent: @Composable () -> Unit = { DefaultMusicListItemTrailingContent(music, onNavigate, onHandlePlayerActions, playlistDropdownMenuItem) },
    isSelected: Boolean = false
) {

    val context = LocalContext.current
    val image = rememberAsyncImagePainter(ImageUtils.imageRequester(music.artUri, context))
    val imageState by image.state.collectAsStateWithLifecycle()

    val bgColor by animateColorAsState(
        targetValue = if (musicState.track.uri.toString() == music.uri.toString() && musicState.isPlayerReady) {
            MaterialTheme.colorScheme.primaryContainer.copy(0.1f)
        } else {
            Color.Transparent
        }
    )

    Surface(
        onClick = { onShortClick(music.mediaId) },
        shape = RoundedCornerShape(24.dp),
        color = bgColor,
        contentColor = contentColorFor(bgColor)
    ) {
        Row(
            modifier = modifier
                .padding(vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedContent(
                targetState = isSelected,
                transitionSpec = { scaleIn() togetherWith scaleOut() },
                modifier = Modifier.padding(start = 10.dp)
            ) {
                if (it) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(MaterialShapes.Cookie9Sided.toShape())
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.check),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                } else {
                    when (imageState) {
                        is AsyncImagePainter.State.Error -> {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.music_note_rounded),
                                    contentDescription = null,
                                    tint = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)

                                )
                            }
                        }

                        else -> {
                            AsyncImage(
                                model = ImageUtils.imageRequester(music.artUri, context),
                                contentDescription = stringResource(R.string.artwork),
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = music.title,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    modifier = Modifier.basicMarquee()
                )
                Text(
                    text = music.artist,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLargeEmphasized,
                    modifier = Modifier.basicMarquee()
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) { trailingContent() }
        }
    }
}

@Composable
private fun DefaultMusicListItemTrailingContent(
    track: CuteTrack,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    playlistDropdownMenuItem: @Composable () -> Unit = { AddToPlaylistDropdownItem(track) },
) {

    val currentScreen = LocalScreen.current
    var isDropDownExpanded by remember { mutableStateOf(false) }


    if (currentScreen is Screen.AlbumsDetails && track.trackNumber != 0) {
        Badge(
            containerColor = MaterialTheme.colorScheme.tertiary
        ) {
            Text(
                text = track.trackNumber.toString(),
                color = MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier.padding(3.dp)
            )
        }
    }


    IconButton(
        onClick = { isDropDownExpanded = true },
        shapes = IconButtonDefaults.shapes()
    ) {
        Icon(
            painter = painterResource(R.drawable.more_vert),
            contentDescription = null
        )
    }

    TrackDropdownMenu(
        track = track,
        isExpanded = isDropDownExpanded,
        onDismissRequest = { isDropDownExpanded = false },
        onNavigate = onNavigate,
        onHandlePlayerActions = onHandlePlayerActions,
        playlistDropdownMenuItem = playlistDropdownMenuItem
    )
}

@Composable
private fun TrackDropdownMenu(
    track: CuteTrack,
    isExpanded: Boolean,
    onDismissRequest: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    playlistDropdownMenuItem: @Composable () -> Unit = { AddToPlaylistDropdownItem(track) }
) {

    val context = LocalContext.current
    val currentScreen = LocalScreen.current
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showPlaylistDialog by remember { mutableStateOf(false) }
    var showDeletionDialog by remember { mutableStateOf(false) }

    val deleteSongLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode != Activity.RESULT_OK) {
                Toast.makeText(
                    context,
                    context.getString(R.string.error_deleting_song),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    if (showDetailsDialog) {
        MusicDetailsDialog(
            track = track,
            onDismissRequest = { showDetailsDialog = false }
        )
    }

    if (showPlaylistDialog) {
        PlaylistPicker(
            mediaId = listOf(track.mediaId),
            onDismissRequest = { showPlaylistDialog = false }
        )
    }

    if (showDeletionDialog) {
        DeletionDialog(
            onDismissRequest = { showDeletionDialog = false },
            onDelete = {
                val intentSender = MediaStore.createDeleteRequest(
                    context.contentResolver,
                    listOf(track.uri)
                ).intentSender

                deleteSongLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = ImageUtils.imageRequester(track.artUri, context),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(15.dp)
                        .clip(RoundedCornerShape(15)),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    modifier = Modifier.basicMarquee()
                )
                Text(
                    text = track.artist,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmallEmphasized,
                    modifier = Modifier.basicMarquee()
                )

            }
        }
    }

    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(24.dp)
    ) {
        CuteDropdownMenuItem(
            onClick = { showDetailsDialog = true },
            text = {
                Text(stringResource(R.string.details))
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.info_rounded),
                    contentDescription = null
                )
            }
        )
        CuteDropdownMenuItem(
            onClick = {
                onDismissRequest()
                onNavigate(Screen.MetadataEditor(track.path, track.uri.toString()))
            },
            text = {
                Text(stringResource(R.string.edit))
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.edit_rounded),
                    contentDescription = null
                )
            }
        )
        CuteDropdownMenuItem(
            onClick = { onHandlePlayerActions(PlayerActions.AddToQueue(track)) },
            text = {
                Text(stringResource(R.string.add_queue))
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.add),
                    contentDescription = null
                )
            }
        )
        CuteDropdownMenuItem(
            onClick = {
                onDismissRequest()
                onNavigate(
                    Screen.AlbumsDetails(track.album)
                )
            },
            text = {
                Text("${stringResource(R.string.go_to)} ${track.album}")
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(androidx.media3.session.R.drawable.media3_icon_album),
                    contentDescription = null
                )
            },
            visible = currentScreen !is Screen.AlbumsDetails
        )
        CuteDropdownMenuItem(
            onClick = {
                onDismissRequest()
                onNavigate(
                    Screen.ArtistsDetails(track.artist)
                )
            },
            text = {
                Text("${stringResource(R.string.go_to)} ${track.artist}")
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.artist_rounded),
                    contentDescription = null
                )
            },
            visible = currentScreen !is Screen.ArtistsDetails
        )
        playlistDropdownMenuItem()
        CuteDropdownMenuItem(
            onClick = {
                ShareCompat.IntentBuilder(context)
                    .setType("audio/*")
                    .setStream(Uri.parse(track.path)) // this instead of passing allows to see the file name in the share sheet
                    .setChooserTitle("Share track")
                    .startChooser()
            },
            text = {
                Text(
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
@Composable
fun SafMusicListItem(
    modifier: Modifier = Modifier,
    music: CuteTrack,
    onShortClick: (albumName: String) -> Unit,
    currentMusicUri: String,
    onDeleteFromSaf: () -> Unit,
    isPlayerReady: Boolean,
    showTrackNumber: Boolean = false,
    playlistDropdownMenuItem: @Composable () -> Unit = { AddToPlaylistDropdownItem(music) },
) {

    val context = LocalContext.current
    var isDropDownExpanded by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showPlaylistDialog by remember { mutableStateOf(false) }
    val bgColor by animateColorAsState(
        targetValue = if (currentMusicUri == music.uri.toString() && isPlayerReady) {
            MaterialTheme.colorScheme.surfaceContainer
        } else {
            Color.Transparent
        },
        label = "Background Color",
        animationSpec = tween(500)
    )


    if (showDetailsDialog) {
        MusicDetailsDialog(
            track = music,
            onDismissRequest = { showDetailsDialog = false }
        )
    }

    if (showPlaylistDialog) {
        PlaylistPicker(
            mediaId = listOf(music.mediaId),
            onDismissRequest = { showPlaylistDialog = false }
        )
    }

    DropdownMenuItem(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(bgColor),
        contentPadding = PaddingValues(0.dp),
        onClick = { onShortClick(music.mediaId) },
        leadingIcon = {
            AsyncImage(
                model = ImageUtils.imageRequester(music.artUri, context),
                stringResource(R.string.artwork),
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(45.dp)
                    .clip(RoundedCornerShape(5.dp)),
                contentScale = ContentScale.Crop,
            )
        },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 15.dp)
            ) {
                Text(
                    text = music.title,
                    maxLines = 1,
                    modifier = Modifier.basicMarquee(),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = music.artist,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.85f),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        },
        trailingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showTrackNumber && music.trackNumber != 0) {
                    Box(
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiary)
                            .wrapContentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = music.trackNumber.toString(),
                            color = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier.padding(3.dp)
                        )
                    }
                }

                IconButton(
                    onClick = { isDropDownExpanded = true }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.more_vert),
                        contentDescription = null
                    )
                }
                DropdownMenu(
                    expanded = isDropDownExpanded,
                    onDismissRequest = { isDropDownExpanded = false },
                    shape = RoundedCornerShape(24.dp)
                ) {
                    CuteDropdownMenuItem(
                        onClick = { showDetailsDialog = true },
                        text = {
                            Text(stringResource(R.string.details))
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.info_rounded),
                                contentDescription = null
                            )
                        }
                    )
                    playlistDropdownMenuItem()
                    CuteDropdownMenuItem(
                        onClick = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_STREAM, music.uri)
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
                            Text(
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
                    CuteDropdownMenuItem(
                        onClick = onDeleteFromSaf,
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