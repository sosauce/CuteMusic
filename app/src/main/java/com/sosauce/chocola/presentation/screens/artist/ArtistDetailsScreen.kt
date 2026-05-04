@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.chocola.presentation.screens.artist

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberIsLandscape
import com.sosauce.chocola.data.datastore.rememberSortTracksAscending
import com.sosauce.chocola.data.datastore.rememberTrackSort
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.navigation.Screen
import com.sosauce.chocola.presentation.screens.album.components.NumberOfTracks
import com.sosauce.chocola.presentation.screens.artist.components.ArtistHeader
import com.sosauce.chocola.presentation.screens.artist.components.NumberOfAlbums
import com.sosauce.chocola.presentation.shared_components.CuteSearchbar
import com.sosauce.chocola.presentation.shared_components.MusicListItem
import com.sosauce.chocola.presentation.shared_components.NoXFound
import com.sosauce.chocola.presentation.shared_components.SortingDropdownMenu
import com.sosauce.chocola.presentation.shared_components.TracksSelectedBar
import com.sosauce.chocola.presentation.shared_components.animations.AnimatedFab
import com.sosauce.chocola.utils.ImageUtils
import com.sosauce.chocola.utils.barsContentTransform
import com.sosauce.chocola.utils.selfAlignHorizontally
import com.sosauce.sweetselect.rememberSweetSelectState

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
    val multiSelectState = rememberSweetSelectState<CuteTrack>()


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
                    targetState = multiSelectState.isInSelectionMode,
                    transitionSpec = { barsContentTransform }
                ) {
                    if (it) {
                        TracksSelectedBar(
                            modifier = Modifier.selfAlignHorizontally(),
                            tracks = state.tracks,
                            multiSelectState = multiSelectState,
                            onHandlePlayerActions = onHandlePlayerAction
                        )
                    } else {
                        CuteSearchbar(
                            modifier = Modifier.selfAlignHorizontally(),
                            musicState = musicState,
                            onHandlePlayerActions = onHandlePlayerAction,
                            showSearchField = false,
                            onNavigate = onNavigate,
                            onNavigateUp = onNavigateUp,
                            fab = {
                                AnimatedFab(
                                    onClick = {
                                        onHandlePlayerAction(
                                            PlayerActions.Play(
                                                index = 0,
                                                tracks = state.tracks,
                                                random = true
                                            )
                                        )
                                    },
                                    icon = R.drawable.shuffle
                                )
                            }
                        )
                    }
                }
            }
        ) { paddingValues ->

            LazyColumn(
                state = lazyState,
                contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding()),
            ) {

                item(
                    key = "Header"
                ) {
                    ArtistHeader(
                        artist = state.artist,
                        tracks = state.tracks,
                        onHandlePlayerActions = onHandlePlayerAction
                    )
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
                                    .clip(MaterialTheme.shapes.extraLarge)
                                    .clickable { onNavigate(Screen.AlbumsDetails(album.name)) },
                                contentAlignment = Alignment.BottomStart
                            ) {
                                AsyncImage(
                                    model = ImageUtils.getAlbumArt(album.id),
                                    contentDescription = stringResource(R.string.artwork),
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                                            )
                                        )
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.Bottom
                                ) {
                                    Text(
                                        text = album.name,
                                        style = MaterialTheme.typography.titleMediumEmphasized,
                                        modifier = Modifier.basicMarquee()
                                    )
                                    Text(
                                        text = album.artist,
                                        style = MaterialTheme.typography.bodyMediumEmphasized,
                                        modifier = Modifier.basicMarquee()
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
                            track = music,
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
                } else {
                    item {
                        NoXFound(
                            headlineText = R.string.no_music_title,
                            bodyText = R.string.better_luck_next_time,
                            icon = R.drawable.music_note_rounded
                        )
                    }
                }
            }
        }
    }

}