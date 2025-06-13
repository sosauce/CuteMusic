@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.ui.screens.playlists

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.actions.PlaylistActions
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.main.components.SortingDropdownMenu
import com.sosauce.cutemusic.ui.shared_components.CuteDropdownMenuItem
import com.sosauce.cutemusic.ui.shared_components.CuteSearchbar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.PlaylistViewModel
import com.sosauce.cutemusic.utils.SharedTransitionKeys
import com.sosauce.cutemusic.utils.rememberSearchbarAlignment
import com.sosauce.cutemusic.utils.showCuteSearchbar
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PlaylistsScreen(
    currentlyPlaying: String,
    isCurrentlyPlaying: Boolean,
    onNavigate: (Screen) -> Unit,
    isPlayerReady: Boolean,
    onHandlePlayerAction: (PlayerActions) -> Unit,
) {
    val context = LocalContext.current
    val playlistViewModel = koinViewModel<PlaylistViewModel>()
    val playlists by playlistViewModel.allPlaylists.collectAsStateWithLifecycle()
    var showPlaylistCreatorDialog by remember { mutableStateOf(false) }
    var showPlaylistActionsDialog by remember { mutableStateOf(false) }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    val buttonOrientation by animateFloatAsState(
        targetValue = if (showPlaylistActionsDialog) 45f else 0f
    )
    val state = rememberLazyListState()
    var query by remember { mutableStateOf("") }
    var isSortedByASC by rememberSaveable { mutableStateOf(true) }
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

    val importPlaylistLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            if (!it.toString().endsWith(".m3u")) {
                Toast.makeText(
                    context,
                    context.getString(R.string.not_m3u_file),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                playlistViewModel.handlePlaylistActions(
                    PlaylistActions.ImportM3uPlaylist(it)
                )
                showPlaylistActionsDialog = false
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
                contentPadding = paddingValues,
                state = state
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
                            modifier = Modifier.animateItem(),
                            playlist = playlist,
                            onHandlePlaylistActions = playlistViewModel::handlePlaylistActions,
                            onClickPlaylist = { onNavigate(Screen.PlaylistDetails(playlist.id)) }
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = state.showCuteSearchbar,
                modifier = Modifier.align(rememberSearchbarAlignment()),
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                CuteSearchbar(
                    query = query,
                    onQueryChange = { query = it },
                    isScrolling = state.isScrollInProgress,
                    modifier = Modifier.align(rememberSearchbarAlignment()),
                    trailingIcon = {
                        IconButton(
                            onClick = { sortMenuExpanded = true }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Sort,
                                contentDescription = stringResource(R.string.sort)
                            )
                        }
                        SortingDropdownMenu(
                            expanded = sortMenuExpanded,
                            onDismissRequest = { sortMenuExpanded = false },
                            isSortedByASC = isSortedByASC,
                            onChangeSorting = { isSortedByASC = it }
                        )
                    },
                    currentlyPlaying = currentlyPlaying,
                    onHandlePlayerActions = onHandlePlayerAction,
                    isPlaying = isCurrentlyPlaying,
                    isPlayerReady = isPlayerReady,
                    onNavigate = onNavigate,
                    fab = {
                        Column {
                            SmallFloatingActionButton(
                                onClick = { showPlaylistActionsDialog = true },
                                modifier = Modifier.sharedBounds(
                                    sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.FAB),
                                    animatedVisibilityScope = LocalNavAnimatedContentScope.current
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = stringResource(R.string.create_playlist),
                                    modifier = Modifier.rotate(buttonOrientation)
                                )
                            }
                            DropdownMenu(
                                expanded = showPlaylistActionsDialog,
                                onDismissRequest = { showPlaylistActionsDialog = false },
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                CuteDropdownMenuItem(
                                    onClick = { importPlaylistLauncher.launch(arrayOf("*/*")) },
                                    text = { CuteText(stringResource(R.string.import_playlist)) },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.resource_import),
                                            contentDescription = null
                                        )
                                    }
                                )
                                CuteDropdownMenuItem(
                                    onClick = {
                                        showPlaylistCreatorDialog = true
                                        showPlaylistActionsDialog = false
                                    },
                                    text = { CuteText(stringResource(R.string.create_playlist)) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Rounded.PlaylistAdd,
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}