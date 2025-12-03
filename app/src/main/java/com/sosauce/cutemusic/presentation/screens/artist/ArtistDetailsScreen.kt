@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.cutemusic.presentation.screens.artist

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.data.datastore.rememberTrackSort
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.presentation.navigation.Screen
import com.sosauce.cutemusic.presentation.screens.album.components.NumberOfTracks
import com.sosauce.cutemusic.presentation.screens.artist.components.ArtistHeader
import com.sosauce.cutemusic.presentation.screens.artist.components.ArtistHeaderLandscape
import com.sosauce.cutemusic.presentation.screens.artist.components.NumberOfAlbums
import com.sosauce.cutemusic.presentation.screens.main.components.SortingDropdownMenu
import com.sosauce.cutemusic.presentation.shared_components.CuteDropdownMenuItem
import com.sosauce.cutemusic.presentation.shared_components.CuteSearchbar
import com.sosauce.cutemusic.presentation.shared_components.LocalMusicListItem
import com.sosauce.cutemusic.presentation.shared_components.SelectedBar
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.TrackSort
import com.sosauce.cutemusic.utils.ordered
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
    val selectedTracks = remember { mutableStateListOf<String>() }
    var showTrackSort by remember { mutableStateOf(false) }
    var sortTracksAsc by rememberSaveable { mutableStateOf(true) }
    var trackSort by rememberTrackSort()

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
                    targetState = selectedTracks.isEmpty(),
                    transitionSpec = { scaleIn() togetherWith scaleOut() },
                    modifier = Modifier.selfAlignHorizontally()
                ) {
                    if (it) {
                        CuteSearchbar(
                            musicState = musicState,
                            onHandlePlayerActions = onHandlePlayerAction,
                            showSearchField = false,
                            onNavigate = onNavigate,
                            onNavigateUp = onNavigateUp
                        )
                    } else {
                        SelectedBar(
                            selectedElements = selectedTracks,
                            onClearSelected = selectedTracks::clear
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
                            musics = state.tracks,
                            onHandlePlayerActions = onHandlePlayerAction
                        )
                    } else {
                        ArtistHeader(
                            artist = state.artist,
                            musics = state.tracks,
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

//                        LazyRow {
//                            items(
//                                items = albums,
//                                key = { it.id }
//                            ) { album ->
//                                AlbumCard(
//                                    album = album,
//                                    onClick = { onNavigate(Screen.AlbumsDetails(album.id)) }
//                                )
//                            }
//                        }
                    }
                }

                if (state.tracks.isNotEmpty()) {
                    item(
                        key = "NbTracks"
                    ) {
                        NumberOfTracks(
                            size = state.tracks.size,
                            onAddToSelected = { selectedTracks.addAll(state.tracks.map { it.mediaId }) },
                            sortMenu = {
                                Column {
                                    IconButton(
                                        onClick = { showTrackSort = !showTrackSort },
                                        shapes = IconButtonDefaults.shapes()
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.sort),
                                            contentDescription = null
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = showTrackSort,
                                        onDismissRequest = { showTrackSort = false },
                                        shape = RoundedCornerShape(24.dp)
                                    ) {
                                        SortingDropdownMenu(
                                            isSortedByASC = sortTracksAsc,
                                            onChangeSorting = { sortTracksAsc = it }
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
                                                CuteDropdownMenuItem(
                                                    onClick = { trackSort = i },
                                                    text = { Text(stringResource(text)) },
                                                    leadingIcon = {
                                                        RadioButton(
                                                            selected = trackSort == i,
                                                            onClick = null
                                                        )
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        )
                    }

                    items(
                        items = state.tracks.ordered(
                            sort = TrackSort.entries[trackSort],
                            ascending = sortTracksAsc,
                            query = ""
                        ),
                        key = { it.mediaId }
                    ) { music ->
                        LocalMusicListItem(
                            modifier = Modifier.animateItem(),
                            music = music,
                            musicState = musicState,
                            onShortClick = { mediaId ->
                                if (selectedTracks.isEmpty()) {
                                    onHandlePlayerAction(
                                        PlayerActions.StartArtistPlayback(
                                            artistName = state.artist.name,
                                            mediaId = mediaId
                                        )
                                    )
                                } else {
                                    if (selectedTracks.contains(mediaId)) {
                                        selectedTracks.remove(mediaId)
                                    } else {
                                        selectedTracks.add(mediaId)
                                    }
                                }
                            },
                            onNavigate = onNavigate,
                            isSelected = selectedTracks.contains(music.mediaId),
                            onHandlePlayerActions = onHandlePlayerAction
                        )
                    }
                }
            }
        }
    }

}