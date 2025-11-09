@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.cutemusic.presentation.screens.album

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.MediaItemActions
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.data.datastore.rememberTrackSort
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.presentation.navigation.Screen
import com.sosauce.cutemusic.presentation.screens.album.components.AlbumHeader
import com.sosauce.cutemusic.presentation.screens.album.components.AlbumHeaderLandscape
import com.sosauce.cutemusic.presentation.screens.album.components.NumberOfTracks
import com.sosauce.cutemusic.presentation.screens.main.components.SortingDropdownMenu
import com.sosauce.cutemusic.presentation.shared_components.CuteDropdownMenuItem
import com.sosauce.cutemusic.presentation.shared_components.CuteSearchbar
import com.sosauce.cutemusic.presentation.shared_components.LocalMusicListItem
import com.sosauce.cutemusic.presentation.shared_components.SelectedBar
import com.sosauce.cutemusic.utils.TrackSort
import com.sosauce.cutemusic.utils.ordered
import com.sosauce.cutemusic.utils.selfAlignHorizontally

@Composable
fun SharedTransitionScope.AlbumDetailsScreen(
    state: AlbumDetailsState,
    onNavigateUp: () -> Unit,
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onHandleMediaItemAction: (MediaItemActions) -> Unit,
    onNavigate: (Screen) -> Unit
) {
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

        val sortedMusic = state.tracks.ordered(
            sort = TrackSort.entries[trackSort],
            ascending = sortTracksAsc,
            query = ""
        ).sortedWith(
            compareBy(
                { it.trackNumber == 0 },
                { it.trackNumber }
            )
        )

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
                            onHandlePlayerActions = onHandlePlayerActions,
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
                modifier = Modifier.padding(horizontal = 5.dp),
            ) {
                item(
                    key = "Header"
                ) {
                    if (isLandscape) {
                        AlbumHeaderLandscape(
                            album = state.album,
                            musics = sortedMusic,
                            onHandlePlayerActions = onHandlePlayerActions
                        )
                    } else {
                        AlbumHeader(
                            album = state.album,
                            musics = sortedMusic,
                            onHandlePlayerActions = onHandlePlayerActions
                        )
                    }
                    Spacer(Modifier.height(10.dp))
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
                                        imageVector = Icons.AutoMirrored.Rounded.Sort,
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
                    items = sortedMusic,
                    key = { it.mediaId }
                ) { music ->
                    LocalMusicListItem(
                        modifier = Modifier.animateItem(),
                        music = music,
                        musicState = musicState,
                        onShortClick = { mediaId ->
                            if (selectedTracks.isEmpty()) {
                                onHandlePlayerActions(
                                    PlayerActions.StartAlbumPlayback(
                                        albumName = music.album,
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
                        onHandleMediaItemAction = onHandleMediaItemAction,
                        onHandlePlayerActions = onHandlePlayerActions,
                        onNavigate = onNavigate,
                        isSelected = selectedTracks.contains(music.mediaId)
                    )
                }
            }
        }
    }

}
