@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.ui.screens.playlists

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.actions.PlaylistActions
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.shared_components.CuteSearchbar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.NavigationItem
import com.sosauce.cutemusic.ui.shared_components.PlaylistViewModel
import com.sosauce.cutemusic.ui.shared_components.ScreenSelection
import com.sosauce.cutemusic.utils.rememberSearchbarAlignment
import com.sosauce.cutemusic.utils.rememberSearchbarMaxFloatValue
import com.sosauce.cutemusic.utils.rememberSearchbarRightPadding
import com.sosauce.cutemusic.utils.thenIf
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PlaylistsScreen(
    currentlyPlaying: String,
    isCurrentlyPlaying: Boolean,
    onNavigate: (Screen) -> Unit,
    onNavigationItemClicked: (Int, NavigationItem) -> Unit,
    selectedIndex: Int,
    animatedVisibilityScope: AnimatedVisibilityScope,
    isPlayerReady: Boolean,
    onHandlePlayerAction: (PlayerActions) -> Unit,
) {

    val playlistViewModel = koinViewModel<PlaylistViewModel>()
    val playlists by playlistViewModel.allPlaylists.collectAsStateWithLifecycle()
    var showPlaylistCreatorDialog by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var screenSelectionExpanded by remember { mutableStateOf(false) }
    var isSortedByASC by remember { mutableStateOf(true) } // I prolly should change this
    val float by animateFloatAsState(
        targetValue = if (isSortedByASC) 45f else 135f,
        label = "Arrow Icon Animation"
    )
    val displayPlaylists by remember(isSortedByASC, playlists, query) {
        derivedStateOf {
            if (query.isNotEmpty()) {
                playlists.filter {
                    it.name.contains(
                        other = query,
                        ignoreCase = true
                    ) == true
                }
            } else {
                if (isSortedByASC) playlists
                else playlists.sortedByDescending { it.name }
            }

        }
    }

    if (showPlaylistCreatorDialog) {
        CreatePlaylistDialog { showPlaylistCreatorDialog = false }
    }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(Modifier.fillMaxSize()) {
            if (playlists.isEmpty()) {
                item {
                    CuteText(
                        text = stringResource(id = R.string.no_playlists),
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(16.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                itemsIndexed(
                    items = displayPlaylists,
                    key = { _, playlist -> playlist.id }
                ) { index, playlist ->
                    PlaylistItem(
                        modifier = Modifier
                            .animateItem()
                            .thenIf(index == 0) {
                                Modifier.statusBarsPadding()
                            },
                        playlist = playlist,
                        onDeletePlaylist = { playlistViewModel.handlePlaylistActions(PlaylistActions.DeletePlaylist(playlist)) },
                        onUpsertPlaylist = {
                            playlistViewModel.handlePlaylistActions(
                                PlaylistActions.UpsertPlaylist(it)
                            )
                        },
                        onClickPlaylist = { onNavigate(Screen.PlaylistDetails(playlist.id)) }
                    )
                }
            }
        }

        CuteSearchbar(
            query = query,
            onQueryChange = { query = it },
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth(rememberSearchbarMaxFloatValue())
                .align(rememberSearchbarAlignment())
                .padding(
                    bottom = 5.dp,
                    end = rememberSearchbarRightPadding()
                ),
            placeholder = {
                CuteText(
                    text = stringResource(id = R.string.search_playlists),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),

                    )
            },
            leadingIcon = {
                IconButton(
                    onClick = {
                        screenSelectionExpanded = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                        contentDescription = null
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
            onClickFAB = { showPlaylistCreatorDialog = true },
            isPlaylist = true
        )
    }
}