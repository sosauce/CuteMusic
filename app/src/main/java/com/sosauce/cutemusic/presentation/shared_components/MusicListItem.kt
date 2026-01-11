@file:OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.cutemusic.presentation.shared_components

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.core.app.ShareCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberAllSafTracks
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.presentation.navigation.Screen
import com.sosauce.cutemusic.presentation.screens.playlists.components.PlaylistPicker
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.LocalScreen
import com.sosauce.cutemusic.utils.copyMutate

@Composable
fun MusicListItem(
    modifier: Modifier = Modifier,
    music: CuteTrack,
    musicState: MusicState,
    onShortClick: (mediaId: String) -> Unit,
    onLongClick: (() -> Unit)? = null,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    isSelected: Boolean = false,
    extraOptions: List<MoreOptions> = emptyList()
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
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 0.9f else 1f
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .padding(3.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(bgColor)
            .combinedClickable(
                onClick = { onShortClick(music.mediaId) },
                onLongClick = onLongClick
            )
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
                    SelectedItemLogo()
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
            ) {
                DefaultMusicListItemTrailingContent(
                    track = music,
                    onNavigate = onNavigate,
                    onHandlePlayerActions = onHandlePlayerActions,
                    musicState = musicState,
                    extraOptions = extraOptions
                )
            }
        }
    }
}

@Composable
private fun DefaultMusicListItemTrailingContent(
    track: CuteTrack,
    musicState: MusicState,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    extraOptions: List<MoreOptions> = emptyList()
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
        musicState = musicState,
        isExpanded = isDropDownExpanded,
        onDismissRequest = { isDropDownExpanded = false },
        onNavigate = onNavigate,
        onHandlePlayerActions = onHandlePlayerActions,
        extraOptions = extraOptions
    )
}

@Composable
private fun TrackDropdownMenu(
    track: CuteTrack,
    musicState: MusicState,
    isExpanded: Boolean,
    onDismissRequest: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    extraOptions: List<MoreOptions> = emptyList()
) {

    val context = LocalContext.current
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showDeletionDialog by remember { mutableStateOf(false) }
    var showPlaylistDialog by remember { mutableStateOf(false) }
    var safTracks by rememberAllSafTracks()
    val trackOptions = listOf(
        MoreOptions(
            text = { stringResource(R.string.edit) },
            onClick = {
                onDismissRequest()
                onNavigate(Screen.MetadataEditor(track.path, track.uri.toString()))
            },
            icon = R.drawable.edit_rounded
        ),
        MoreOptions(
            text = { stringResource(R.string.add_queue) },
            onClick = { onHandlePlayerActions(PlayerActions.AddToQueue(track)) },
            icon = R.drawable.add,
            enabled = track !in musicState.loadedMedias,
            disabledText = { stringResource(R.string.already_in_queue) }
        ),
        MoreOptions(
            text = { stringResource(R.string.go_to, track.album) },
            onClick = {
                onDismissRequest()
                onNavigate(
                    Screen.AlbumsDetails(track.album)
                )
            },
            icon = androidx.media3.session.R.drawable.media3_icon_album
        ),
        MoreOptions(
            text = { stringResource(R.string.go_to, track.artist) },
            onClick = {
                onDismissRequest()
                onNavigate(
                    Screen.ArtistsDetails(track.artist)
                )
            },
            icon = R.drawable.artist_rounded
        ),
        MoreOptions(
            text = { stringResource(R.string.add_to_playlist) },
            onClick = { showPlaylistDialog = true },
            icon = R.drawable.playlist_add
        )
    ) + extraOptions
    val onDeleteClick = remember {
        {
            if (track.isSaf) {
                safTracks = safTracks.copyMutate { remove(track.uri.toString()) }
            } else {
                showDeletionDialog = true
            }
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
            track = track,
            onDismissRequest = { showDeletionDialog = false }
        )
    }


    DropdownMenuPopup(
        expanded = isExpanded,
        onDismissRequest = onDismissRequest
    ) {
        DropdownMenuGroup(
            shapes = MenuDefaults.groupShapes()
        ) {
            trackOptions.fastForEachIndexed { index, option ->
                DropdownMenuItem(
                    onClick = option.onClick,
                    enabled = option.enabled,
                    shape = when (index) {
                        0 -> MenuDefaults.leadingItemShape
                        trackOptions.lastIndex -> MenuDefaults.trailingItemShape
                        else -> MenuDefaults.middleItemShape
                    },
                    text = {
                        if (option.enabled) {
                            Text(option.text())
                        } else {
                            Text(option.disabledText!!())
                        }
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(option.icon),
                            contentDescription = null
                        )
                    }
                )
            }
        }
        Spacer(Modifier.height(MenuDefaults.GroupSpacing))
        ButtonGroup(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
        ) {
            FilledIconButton(
                onClick = { showDetailsDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)),
                shape = IconButtonDefaults.mediumSquareShape,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.info_filled),
                    contentDescription = null,
                    modifier = Modifier.size(IconButtonDefaults.mediumIconSize)
                )
            }
            FilledIconButton(
                onClick = {
                    ShareCompat.IntentBuilder(context)
                        .setType("audio/*")
                        .setStream(Uri.parse(track.path)) // this instead of passing the path allows to see the file name in the share sheet
                        .setChooserTitle("Share track")
                        .startChooser()
                },
                modifier = Modifier
                    .weight(1f)
                    .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)),
                shape = IconButtonDefaults.mediumSquareShape,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.share_filled),
                    contentDescription = null,
                    modifier = Modifier.size(IconButtonDefaults.mediumIconSize)
                )
            }
            FilledIconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .weight(1f)
                    .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)),
                shape = IconButtonDefaults.mediumSquareShape,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.trash_rounded_filled),
                    contentDescription = null,
                    modifier = Modifier.size(IconButtonDefaults.mediumIconSize)
                )
            }
        }
    }
}


data class MoreOptions(
    val text: @Composable () -> String,
    val onClick: () -> Unit,
    val icon: Int,
    val tint: Color? = null,
    val enabled: Boolean = true,
    val disabledText: (@Composable () -> String)? = null,
)