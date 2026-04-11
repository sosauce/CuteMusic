@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class,
    FlowPreview::class
)

package com.sosauce.chocola.presentation.screens.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberGroupByFolders
import com.sosauce.chocola.data.datastore.rememberHiddenFolders
import com.sosauce.chocola.data.datastore.rememberSortTracksAscending
import com.sosauce.chocola.data.datastore.rememberTrackSort
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.navigation.Screen
import com.sosauce.chocola.presentation.screens.main.components.FolderHeader
import com.sosauce.chocola.presentation.shared_components.CuteActionButton
import com.sosauce.chocola.presentation.shared_components.CuteSearchbar
import com.sosauce.chocola.presentation.shared_components.MusicListItem
import com.sosauce.chocola.presentation.shared_components.NoResult
import com.sosauce.chocola.presentation.shared_components.NoXFound
import com.sosauce.chocola.presentation.shared_components.SortingDropdownMenu
import com.sosauce.chocola.presentation.shared_components.TracksSelectedBar
import com.sosauce.chocola.utils.SharedTransitionKeys
import com.sosauce.chocola.utils.addOrRemove
import com.sosauce.chocola.utils.copyMutate
import com.sosauce.chocola.utils.selfAlignHorizontally
import com.sosauce.sweetselect.rememberSweetSelectState
import kotlinx.coroutines.FlowPreview

// https://medium.com/@gregkorossy/hacking-lazylist-in-android-jetpack-compose-38afacb3df67
@Composable
fun SharedTransitionScope.MainScreen(
    state: MainState,
    musicState: MusicState,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerAction: (PlayerActions) -> Unit
) {

    val lazyState = rememberLazyListState()
    var hiddenFolders by rememberHiddenFolders()
    var groupByFolders by rememberGroupByFolders()
    var trackSort by rememberTrackSort()
    var sortTracksAscending by rememberSortTracksAscending()
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
                            onHandlePlayerActions = onHandlePlayerAction
                        )
                    } else {
                        CuteSearchbar(
                            modifier = Modifier.selfAlignHorizontally(),
                            textFieldState = state.textFieldState,
                            musicState = musicState,
                            showSearchField = true,
                            sortingMenu = {
                                SortingDropdownMenu(
                                    isSortedAscending = sortTracksAscending,
                                    onChangeSorting = { sortTracksAscending = it },
                                    topContent = {
                                        DropdownMenuItem(
                                            selected = groupByFolders,
                                            onClick = { groupByFolders = !groupByFolders },
                                            shapes = MenuDefaults.itemShapes(),
                                            colors = MenuDefaults.selectableItemColors(),
                                            text = { Text(stringResource(R.string.group_tracks)) },
                                            trailingIcon = {
                                                if (groupByFolders) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.check),
                                                        contentDescription = null
                                                    )
                                                }
                                            }
                                        )
                                    }
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
                            },
                            onHandlePlayerActions = onHandlePlayerAction,
                            onNavigate = onNavigate,
                            fab = {
                                CuteActionButton(
                                    action = {
                                        onHandlePlayerAction(
                                            PlayerActions.Play(
                                                index = 0,
                                                tracks = state.tracks,
                                                random = true
                                            )
                                        )
                                    },
                                    modifier = Modifier.sharedBounds(
                                        sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.FAB),
                                        animatedVisibilityScope = LocalNavAnimatedContentScope.current
                                    )
                                )
                            }
                        )
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                state = lazyState,
                contentPadding = paddingValues
            ) {
                if (state.tracks.isEmpty() && !state.isSearching) {
                    item {
                        NoXFound(
                            headlineText = R.string.no_music_title,
                            bodyText = R.string.no_music_desc,
                            icon = R.drawable.music_note_rounded
                        )
                        Text(
                            text = stringResource(R.string.no_music_tip),
                            style = MaterialTheme.typography.bodySmallEmphasized.copy(
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier
                                .padding(top = 15.dp)
                                .selfAlignHorizontally()
                        )
                    }
                } else {
                    if (groupByFolders) {
                        val categories = state.tracks
                            .groupBy { it.folder }
                            .toSortedMap()
                            .map {
                                Category(
                                    name = it.key,
                                    tracks = it.value
                                )
                            }
                        if (state.tracks.isEmpty()) {
                            item { NoResult() }
                        } else {
                            categories.fastForEach { category ->
                                item  {
                                    FolderHeader(
                                        category = category,
                                        isHidden = category.name in hiddenFolders,
                                        onToggleVisibility = {
                                            hiddenFolders =
                                                hiddenFolders.copyMutate { addOrRemove(category.name) }
                                        },
                                        onHandlePlayerAction = onHandlePlayerAction
                                    )
                                }
                                if (category.name !in hiddenFolders) {
                                    items(
                                        items = category.tracks,
                                        key = { it.mediaId }
                                    ) { music ->

                                        val isSelected by remember {
                                            derivedStateOf { multiSelectState.isSelected(music) }
                                        }

                                        MusicListItem(
                                            modifier = Modifier
                                                .animateItem()
                                                .padding(
                                                    vertical = 2.dp,
                                                    horizontal = 4.dp
                                                ),
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
                                            isSelected = isSelected,
                                            track = music,
                                            musicState = musicState,
                                            onNavigate = { onNavigate(it) },
                                            onHandlePlayerActions = onHandlePlayerAction
                                        )
                                    }
                                }
                            }
                        }

                    } else {
                        if (state.tracks.isEmpty()) {
                            item { NoResult() }
                        } else {
                            itemsIndexed(
                                items = state.tracks,
                                key = { _, track -> track.mediaId }
                            ) { index, track ->

                                val isSelected by remember {
                                    derivedStateOf { multiSelectState.isSelected(track) }
                                }

                                MusicListItem(
                                    modifier = Modifier.animateItem(),

                                    onShortClick = {
                                        if (multiSelectState.isInSelectionMode) {
                                            multiSelectState.toggle(track)
                                        } else {
                                            onHandlePlayerAction(
                                                PlayerActions.Play(
                                                    index = state.tracks.indexOf(track),
                                                    tracks = state.tracks
                                                )
                                            )
                                        }
                                    },
                                    onLongClick = { multiSelectState.toggle(track) },
                                    track = track,
                                    musicState = musicState,
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
    }

}

data class Category(
    val name: String,
    val tracks: List<CuteTrack>
)




