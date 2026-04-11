@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.chocola.presentation.screens.album

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberIsLandscape
import com.sosauce.chocola.data.datastore.rememberSortTracksAscending
import com.sosauce.chocola.data.datastore.rememberTrackSort
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.navigation.Screen
import com.sosauce.chocola.presentation.screens.album.components.AlbumHeader
import com.sosauce.chocola.presentation.screens.album.components.AlbumHeaderLandscape
import com.sosauce.chocola.presentation.screens.album.components.NumberOfTracks
import com.sosauce.chocola.presentation.shared_components.CuteSearchbar
import com.sosauce.chocola.presentation.shared_components.MusicListItem
import com.sosauce.chocola.presentation.shared_components.SortingDropdownMenu
import com.sosauce.chocola.presentation.shared_components.TracksSelectedBar
import com.sosauce.chocola.utils.TrackSort
import com.sosauce.chocola.utils.ordered
import com.sosauce.chocola.utils.selfAlignHorizontally
import com.sosauce.sweetselect.rememberSweetSelectState

@Composable
fun SharedTransitionScope.AlbumDetailsScreen(
    state: AlbumDetailsState,
    onNavigateUp: () -> Unit,
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val lazyState = rememberLazyListState()
    val isLandscape = rememberIsLandscape()
    var sortTracksAsc by rememberSortTracksAscending()
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
                    targetState = multiSelectState.isInSelectionMode
                ) {
                    if (it) {
                        TracksSelectedBar(
                            modifier = Modifier.selfAlignHorizontally(),
                            tracks = state.tracks,
                            multiSelectState = multiSelectState,
                            onHandlePlayerActions = onHandlePlayerActions
                        )
                    } else {
                        CuteSearchbar(
                            modifier = Modifier.selfAlignHorizontally(),
                            musicState = musicState,
                            onHandlePlayerActions = onHandlePlayerActions,
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
                modifier = Modifier.padding(horizontal = 5.dp),
            ) {
                item(
                    key = "Header"
                ) {
                    if (isLandscape) {
                        AlbumHeaderLandscape(
                            album = state.album,
                            tracks = state.tracks,
                            onHandlePlayerActions = onHandlePlayerActions
                        )
                    } else {
                        AlbumHeader(
                            album = state.album,
                            tracks = state.tracks,
                            onHandlePlayerActions = onHandlePlayerActions
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    NumberOfTracks(
                        size = state.tracks.size,
                        sortMenu = {
                            SortingDropdownMenu(
                                isSortedAscending = sortTracksAsc,
                                onChangeSorting = { sortTracksAsc = it }
                            ) {
                                repeat(6) { i ->
                                    val text = when (i) {
                                        0 -> R.string.title
                                        1 -> R.string.artist
                                        2 -> R.string.album
                                        3 -> R.string.year
                                        4 -> R.string.date_modified
                                        5 -> R.string.as_added
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
                                onHandlePlayerActions(
                                    PlayerActions.Play(
                                        index = state.tracks.indexOf(music),
                                        tracks = state.tracks
                                    )
                                )
                            }
                        },
                        onLongClick = { multiSelectState.toggle(music) },
                        onHandlePlayerActions = onHandlePlayerActions,
                        onNavigate = onNavigate,
                        isSelected = isSelected
                    )
                }
            }
        }
    }

}
