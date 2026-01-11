@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.cutemusic.presentation.screens.artist

import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.data.datastore.rememberSortTracksAscending
import com.sosauce.cutemusic.data.datastore.rememberTrackSort
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.presentation.multiselect.rememberMultiSelectState
import com.sosauce.cutemusic.presentation.navigation.Screen
import com.sosauce.cutemusic.presentation.screens.album.components.NumberOfTracks
import com.sosauce.cutemusic.presentation.screens.artist.components.ArtistHeader
import com.sosauce.cutemusic.presentation.screens.artist.components.ArtistHeaderLandscape
import com.sosauce.cutemusic.presentation.screens.artist.components.NumberOfAlbums
import com.sosauce.cutemusic.presentation.screens.playlists.components.PlaylistPicker
import com.sosauce.cutemusic.presentation.shared_components.CuteSearchbar
import com.sosauce.cutemusic.presentation.shared_components.MusicListItem
import com.sosauce.cutemusic.presentation.shared_components.SelectedBar
import com.sosauce.cutemusic.presentation.shared_components.SortingDropdownMenu
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.selfAlignHorizontally

@Composable
fun SharedTransitionScope.ArtistDetailsScreen(
    state: ArtistDetailsState,
    onNavigate: (Screen) -> Unit,
    onNavigateUp: () -> Unit,
    musicState: MusicState,
    onHandlePlayerAction: (PlayerActions) -> Unit
) {
    val context = LocalContext.current
    val lazyState = rememberLazyListState()
    val isLandscape = rememberIsLandscape()
    var sortTracksAscending by rememberSortTracksAscending()
    var trackSort by rememberTrackSort()
    val multiSelectState = rememberMultiSelectState<CuteTrack>()


    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ContainedLoadingIndicator()
        }
    } else {

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                AnimatedContent(
                    targetState = multiSelectState.isInSelectionMode
                ) {
                    if (it) {
                        SelectedBar(
                            modifier = Modifier.selfAlignHorizontally(),
                            multiSelectState = multiSelectState,
                            items = state.tracks,
                            onToggleAll = {
                                if (multiSelectState.selectedItems.size == state.tracks.size) {
                                    multiSelectState.clearSelected()
                                } else {
                                    multiSelectState.toggleAll(state.tracks)
                                }
                            }
                        ) {
                            var showPlaylistDialog by remember { mutableStateOf(false) }
                            val deleteSongLauncher =
                                rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {}

                            if (showPlaylistDialog) {
                                PlaylistPicker(
                                    mediaId = multiSelectState.selectedItems.fastMap { it.mediaId },
                                    onDismissRequest = { showPlaylistDialog = false },
                                    onAddingFinished = multiSelectState::clearSelected
                                )
                            }


                            IconButton(
                                onClick = { showPlaylistDialog = true },
                                shapes = IconButtonDefaults.shapes()
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.playlist_add),
                                    contentDescription = null
                                )
                            }
                            IconButton(
                                onClick = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                        val intentSender = MediaStore.createDeleteRequest(
                                            context.contentResolver,
                                            multiSelectState.selectedItems.fastMap { it.uri }
                                        ).intentSender

                                        deleteSongLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                                    } else {
                                        multiSelectState.selectedItems.fastForEach {
                                            context.contentResolver.delete(it.uri, null, null)
                                        }
                                    }
                                },
                                shapes = IconButtonDefaults.shapes(),
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.trash_rounded_filled),
                                    contentDescription = null
                                )
                            }
                        }
                    } else {
                        CuteSearchbar(
                            modifier = Modifier.selfAlignHorizontally(),
                            musicState = musicState,
                            onHandlePlayerActions = onHandlePlayerAction,
                            showSearchField = false,
                            onNavigate = onNavigate,
                            onNavigateUp = onNavigateUp
                        )
                    }
                }
            }
        ) { paddingValues ->

            LazyColumn(
                state = lazyState,
                contentPadding = paddingValues,
                modifier = Modifier.padding(horizontal = 5.dp)
            ) {

                item(
                    key = "Header"
                ) {
                    if (isLandscape) {
                        ArtistHeaderLandscape(
                            artist = state.artist,
                            tracks = state.tracks,
                            onHandlePlayerActions = onHandlePlayerAction
                        )
                    } else {
                        ArtistHeader(
                            artist = state.artist,
                            tracks = state.tracks,
                            onHandlePlayerActions = onHandlePlayerAction
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                }

                if (state.albums.isNotEmpty()) {
                    item(
                        key = "Albums"
                    ) {
                        NumberOfAlbums(state.artist.numberAlbums)

                        HorizontalMultiBrowseCarousel(
                            state = rememberCarouselState { state.albums.count() },
                            preferredItemWidth = 186.dp,
                            itemSpacing = 8.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 16.dp, bottom = 16.dp)
                        ) { index ->
                            val album = state.albums[index]

                            Box(
                                modifier = Modifier
                                    .height(200.dp)
                                    .sharedElement(
                                        sharedContentState = rememberSharedContentState(key = album.id),
                                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                                    )
                                    .maskClip(MaterialTheme.shapes.extraLarge)
                                    .clickable { onNavigate(Screen.AlbumsDetails(album.name)) },
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                AsyncImage(
                                    model = ImageUtils.imageRequester(
                                        ImageUtils.getAlbumArt(album.id)
                                            ?: androidx.media3.session.R.drawable.media3_icon_album,
                                        context
                                    ),
                                    contentDescription = stringResource(R.string.artwork),
                                    contentScale = ContentScale.Crop
                                )
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = album.name,
                                        style = MaterialTheme.typography.headlineSmallEmphasized,
                                        modifier = Modifier.dropShadow(
                                            shape = RoundedCornerShape(10.dp),
                                            shadow = Shadow(20.dp)
                                        )
                                    )
                                    Text(
                                        text = album.artist,
                                        style = MaterialTheme.typography.bodyMediumEmphasized.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        modifier = Modifier.dropShadow(
                                            shape = RoundedCornerShape(10.dp),
                                            shadow = Shadow(20.dp)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                if (state.tracks.isNotEmpty()) {
                    item(
                        key = "NbTracks"
                    ) {
                        NumberOfTracks(
                            size = state.tracks.size,
                            sortMenu = {
                                SortingDropdownMenu(
                                    isSortedAscending = sortTracksAscending,
                                    onChangeSorting = { sortTracksAscending = it },
                                ) {
                                    repeat(5) { i ->
                                        val text = when (i) {
                                            0 -> R.string.title
                                            1 -> R.string.artist
                                            2 -> R.string.album
                                            3 -> R.string.year
                                            4 -> R.string.date_modified
                                            else -> throw IndexOutOfBoundsException()
                                        }

                                        DropdownMenuItem(
                                            selected = trackSort == i,
                                            onClick = { trackSort = i },
                                            shapes = MenuDefaults.itemShapes(),
                                            colors = MenuDefaults.selectableItemColors(),
                                            text = { Text(stringResource(text)) },
                                            trailingIcon = {
                                                if (trackSort == i) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.check),
                                                        contentDescription = null
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        )
                    }

                    items(
                        items = state.tracks,
                        key = { it.mediaId }
                    ) { music ->

                        val isSelected by remember {
                            derivedStateOf { multiSelectState.isSelected(music) }
                        }

                        MusicListItem(
                            modifier = Modifier.animateItem(),
                            music = music,
                            musicState = musicState,
                            onShortClick = {
                                if (multiSelectState.isInSelectionMode) {
                                    multiSelectState.toggle(music)
                                } else {
                                    onHandlePlayerAction(
                                        PlayerActions.Play(
                                            index = state.tracks.indexOf(music),
                                            tracks = state.tracks
                                        )
                                    )
                                }
                            },
                            onLongClick = { multiSelectState.toggle(music) },
                            onNavigate = onNavigate,
                            isSelected = isSelected,
                            onHandlePlayerActions = onHandlePlayerAction
                        )
                    }
                }
            }
        }
    }

}