@file:OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.cutemusic.ui.shared_components

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
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
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.MediaItemActions
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.playlists.PlaylistPicker
import com.sosauce.cutemusic.utils.CurrentScreen
import com.sosauce.cutemusic.utils.ImageUtils
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
fun SharedTransitionScope.LocalMusicListItem(
    modifier: Modifier = Modifier,
    music: MediaItem,
    onShortClick: (mediaId: String) -> Unit,
    onNavigate: (Screen) -> Unit,
    currentMusicUri: String,
    onHandleMediaItemAction: (MediaItemActions) -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onLoadMetadata: (String, Uri) -> Unit,
    playlistDropdownMenuItem: @Composable () -> Unit = { AddToPlaylistDropdownItem(music) },
    isPlayerReady: Boolean,
    showTrackNumber: Boolean = false,
    isSelected: Boolean = false
) {

    val context = LocalContext.current
    val uri = remember { music.mediaMetadata.extras?.getString("uri")?.toUri() ?: Uri.EMPTY }
    val bgColor by animateColorAsState(
        targetValue = if (currentMusicUri == uri.toString() && isPlayerReady) {
            MaterialTheme.colorScheme.surfaceContainer
        } else {
            Color.Transparent
        },
        animationSpec = tween(500)
    )
    var isDropDownExpanded by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    val path = remember { music.mediaMetadata.extras?.getString("path") ?: "" }
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
    var showPlaylistDialog by remember { mutableStateOf(false) }

    if (showDetailsDialog) {
        MusicDetailsDialog(
            music = music,
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
            AnimatedContent(
                targetState = isSelected,
                transitionSpec = { scaleIn() togetherWith scaleOut() }
            ) {
                if (it) {
                    Box(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .size(45.dp)
                            .clip(MaterialShapes.Cookie9Sided.toShape())
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialShapes.Cookie9Sided.toShape()
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    AsyncImage(
                        model = ImageUtils.imageRequester(music.mediaMetadata.artworkUri),
                        contentDescription = stringResource(R.string.artwork),
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .size(45.dp)
//                            .sharedElement(
//                                sharedContentState = rememberSharedContentState(music.mediaId),
//                                animatedVisibilityScope = LocalNavAnimatedContentScope.current
//                            )
                            .clip(RoundedCornerShape(5.dp)),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 15.dp)
            ) {
                CuteText(
                    text = music.mediaMetadata.title.toString(),
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.basicMarquee()
                )
                CuteText(
                    text = music.mediaMetadata.artist.toString(),
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.85f),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.basicMarquee()
                )
            }
        },
        trailingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showTrackNumber && music.mediaMetadata.trackNumber != null && music.mediaMetadata.trackNumber != 0) {
                    Box(
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiary)
                            .wrapContentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CuteText(
                            text = music.mediaMetadata.trackNumber.toString(),
                            color = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier.padding(3.dp)
                        )
                    }
                }


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
                    CuteDropdownMenuItem(
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
                    CuteDropdownMenuItem(
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
                    CuteDropdownMenuItem(
                        onClick = { onHandlePlayerActions(PlayerActions.AddToQueue(music)) },
                        text = {
                            CuteText(stringResource(R.string.add_queue))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null
                            )
                        }
                    )
                    CuteDropdownMenuItem(
                        onClick = {
                            isDropDownExpanded = false
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
                        },
                        visible = CurrentScreen.screen != Screen.AlbumsDetails
                    )
                    CuteDropdownMenuItem(
                        onClick = {
                            isDropDownExpanded = false
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
                        },
                        visible = CurrentScreen.screen != Screen.ArtistsDetails
                    )
                    playlistDropdownMenuItem()
                    CuteDropdownMenuItem(
                        onClick = {
                            onHandleMediaItemAction(
                                MediaItemActions.ShareMediaItem(uri)
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
                    CuteDropdownMenuItem(
                        onClick = {
                            onHandleMediaItemAction(
                                MediaItemActions.DeleteMediaItem(
                                    listOf(uri),
                                    deleteSongLauncher
                                )
                            )
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
    )
}

@Composable
fun ReorderableCollectionItemScope.QueueMusicListItem(
    modifier: Modifier = Modifier,
    music: MediaItem,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    currentMusicUri: String,
) {
    val uri = remember { music.mediaMetadata.extras?.getString("uri")?.toUri() ?: Uri.EMPTY }
    val bgColor by animateColorAsState(
        targetValue = if (currentMusicUri == uri.toString()) {
            MaterialTheme.colorScheme.surfaceContainer
        } else {
            Color.Transparent
        },
        animationSpec = tween(500)
    )

    DropdownMenuItem(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(bgColor),
        contentPadding = PaddingValues(0.dp),
        onClick = {},
        leadingIcon = {
            AsyncImage(
                model = ImageUtils.imageRequester(music.mediaMetadata.artworkUri),
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
                CuteText(
                    text = music.mediaMetadata.title.toString(),
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.basicMarquee()
                )
                CuteText(
                    text = music.mediaMetadata.artist.toString(),
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.85f),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.basicMarquee()
                )
            }
        },
        trailingIcon = {
            Row {
                IconButton(
                    onClick = { onHandlePlayerActions(PlayerActions.RemoveFromQueue(music.mediaId)) }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null
                    )
                }
                IconButton(
                    onClick = {},
                    modifier = Modifier.draggableHandle()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DragHandle,
                        contentDescription = null
                    )
                }
            }
        }
    )
}

@Composable
fun SafMusicListItem(
    modifier: Modifier = Modifier,
    music: MediaItem,
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
    val uri = remember { music.mediaMetadata.extras?.getString("uri")?.toUri() ?: Uri.EMPTY }
    var showPlaylistDialog by remember { mutableStateOf(false) }
    val bgColor by animateColorAsState(
        targetValue = if (currentMusicUri == uri.toString() && isPlayerReady) {
            MaterialTheme.colorScheme.surfaceContainer
        } else {
            Color.Transparent
        },
        label = "Background Color",
        animationSpec = tween(500)
    )


    if (showDetailsDialog) {
        MusicDetailsDialog(
            music = music,
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
                model = ImageUtils.imageRequester(music.mediaMetadata.artworkUri),
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
                CuteText(
                    text = music.mediaMetadata.title.toString(),
                    maxLines = 1,
                    modifier = Modifier.basicMarquee(),
                    style = MaterialTheme.typography.titleMedium
                )
                CuteText(
                    text = music.mediaMetadata.artist.toString(),
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
                if (showTrackNumber && music.mediaMetadata.trackNumber != null && music.mediaMetadata.trackNumber != 0) {
                    Box(
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiary)
                            .wrapContentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CuteText(
                            text = music.mediaMetadata.trackNumber.toString(),
                            color = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier.padding(3.dp)
                        )
                    }
                }

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
                    CuteDropdownMenuItem(
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
                    playlistDropdownMenuItem()
                    CuteDropdownMenuItem(
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
                    CuteDropdownMenuItem(
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
    )
}