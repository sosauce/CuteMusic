@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalSharedTransitionApi::class
)

package com.sosauce.cutemusic.ui.screens.main

import android.app.Activity
import android.content.Intent
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
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberHasSeenTip
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.main.components.ShareOptionsContent
import com.sosauce.cutemusic.ui.shared_components.CuteSearchbar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicDetailsDialog
import com.sosauce.cutemusic.ui.shared_components.NavigationItem
import com.sosauce.cutemusic.ui.shared_components.ScreenSelection
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.rememberSearchbarAlignment
import com.sosauce.cutemusic.utils.rememberSearchbarMaxFloatValue
import com.sosauce.cutemusic.utils.rememberSearchbarRightPadding
import com.sosauce.cutemusic.utils.thenIf

@Composable
fun SharedTransitionScope.MainScreen(
    musics: List<MediaItem>,
    currentlyPlaying: String,
    isCurrentlyPlaying: Boolean,
    onNavigate: (Screen) -> Unit,
    onShortClick: (String) -> Unit,
    onNavigationItemClicked: (Int, NavigationItem) -> Unit,
    selectedIndex: Int,
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
    var screenSelectionExpanded by remember { mutableStateOf(false) }
    var isSortedByASC by remember { mutableStateOf(true) } // I prolly should change this
    val float by animateFloatAsState(
        targetValue = if (isSortedByASC) 45f else 135f,
        label = "Arrow Icon Animation"
    )
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


    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            state = state
        ) {
            if (displayMusics.isEmpty()) {
                item {
                    CuteText(
                        text = stringResource(id = R.string.no_musics_found),
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(16.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                itemsIndexed(
                    items = displayMusics,
                    key = { _, music -> music.mediaId }
                ) { index, music ->
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
                            modifier = Modifier
                                .thenIf(
                                    index == 0,
                                    Modifier.statusBarsPadding()
                                ),
                            isPlayerReady = isPlayerReady
                        )
                    }
                }
            }
        }

        // TODO : How do you make it NOT scroll to the first item when sorting changes !!!!!
        Crossfade(
            targetState = showCuteSearchbar,
            label = "",
            modifier = Modifier.align(rememberSearchbarAlignment())
        ) { visible ->
            if (visible) {
                val transition = rememberInfiniteTransition(label = "Infinite Color Change")
                val color by transition.animateColor(
                    initialValue = LocalContentColor.current,
                    targetValue = MaterialTheme.colorScheme.errorContainer,
                    animationSpec = infiniteRepeatable(
                        tween(500),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = ""
                )
                var hasSeenTip by rememberHasSeenTip()


                CuteSearchbar(
                    query = query,
                    onQueryChange = { query = it },
                    modifier = Modifier
                        .navigationBarsPadding()
                        .fillMaxWidth(rememberSearchbarMaxFloatValue())
                        .padding(
                            bottom = 5.dp,
                            end = rememberSearchbarRightPadding()
                        ),
                    placeholder = {
                        CuteText(
                            text = stringResource(id = R.string.search) + " " + stringResource(
                                id = R.string.music
                            ),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),

                            )
                    },
                    leadingIcon = {
                        IconButton(
                            onClick = {
                                screenSelectionExpanded = true
                                if (!hasSeenTip) {
                                    hasSeenTip = true
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.music_note_rounded),
                                contentDescription = null,
                                tint = if (!hasSeenTip) color else LocalContentColor.current
                            )
                        }


                        DropdownMenu(
                            expanded = screenSelectionExpanded,
                            onDismissRequest = { screenSelectionExpanded = false },
                            modifier = Modifier
                                .width(180.dp)
                                .background(color = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            ScreenSelection(
                                onNavigationItemClicked = onNavigationItemClicked,
                                selectedIndex = selectedIndex
                            )
                        }
                    },
                    trailingIcon = {
                        Row {
                            IconButton(
                                onClick = { isSortedByASC = !isSortedByASC }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowUpward,
                                    contentDescription = null,
                                    modifier = Modifier.rotate(float)
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
                        }
                    },
                    currentlyPlaying = currentlyPlaying,
                    onHandlePlayerActions = onHandlePlayerAction,
                    isPlaying = isCurrentlyPlaying,
                    animatedVisibilityScope = animatedVisibilityScope,
                    isPlayerReady = isPlayerReady,
                    onNavigate = { onNavigate(Screen.NowPlaying) },
                    onClickFAB = { onHandlePlayerAction(PlayerActions.PlayRandom) }
                )
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
    isPlayerReady: Boolean
) {

    val context = LocalContext.current
    var isDropDownExpanded by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showShareOptions by remember { mutableStateOf(false) }
    val uri = remember { Uri.parse(music.mediaMetadata.extras?.getString("uri")) }
    val path = remember { music.mediaMetadata.extras?.getString("path") }
    val isPlaying = currentMusicUri == music.mediaMetadata.extras?.getString("uri")
    val bgColor by animateColorAsState(
        targetValue = if (isPlaying && isPlayerReady) {
            MaterialTheme.colorScheme.surfaceContainer
        } else {
            MaterialTheme.colorScheme.background
        },
        label = "Background Color",
        animationSpec = tween(500)
    )
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
                    .size(45.dp),
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
                                imageVector = Icons.Rounded.ErrorOutline,
                                contentDescription = null,
                                modifier = Modifier.rotate(180f)
                            )
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            isDropDownExpanded = false
                            onLoadMetadata(path ?: "", uri)
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
                            CuteText(stringResource(R.string.go_to) + music.mediaMetadata.albumTitle)
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
                            CuteText(stringResource(R.string.go_to) + music.mediaMetadata.artist)
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.artist_rounded),
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
}



