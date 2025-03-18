@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.ui.screens.playlists

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import com.sosauce.cutemusic.ui.shared_components.CuteActionButton
import com.sosauce.cutemusic.ui.shared_components.CuteSearchbar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.PlaylistViewModel
import com.sosauce.cutemusic.utils.rememberSearchbarAlignment
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PlaylistsScreen(
    currentlyPlaying: String,
    isCurrentlyPlaying: Boolean,
    onNavigate: (Screen) -> Unit,
    onNavigationItemClicked: (Screen) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    isPlayerReady: Boolean,
    onHandlePlayerAction: (PlayerActions) -> Unit,
) {

    val playlistViewModel = koinViewModel<PlaylistViewModel>()
    val playlists by playlistViewModel.allPlaylists.collectAsStateWithLifecycle()
    var showPlaylistCreatorDialog by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
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
                    )
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

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = paddingValues
            ) {
                if (playlists.isEmpty()) {
                    item {
                        CuteText(
                            text = stringResource(id = R.string.no_playlists),
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    items(
                        items = displayPlaylists,
                        key = { it.id }
                    ) { playlist ->
                        PlaylistItem(
                            modifier = Modifier
                                .animateItem(),
                            playlist = playlist,
                            onDeletePlaylist = {
                                playlistViewModel.handlePlaylistActions(
                                    PlaylistActions.DeletePlaylist(playlist)
                                )
                            },
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
                modifier = Modifier.align(rememberSearchbarAlignment()),
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
                onNavigate = onNavigate,
                fab = {
                    CuteActionButton(
                        modifier = Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "fab"),
                            animatedVisibilityScope = animatedVisibilityScope
                        ),
                        imageVector = Icons.Rounded.Add
                    ) { showPlaylistCreatorDialog = true }
                }
            )
        }
    }
}