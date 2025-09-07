@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.playlists

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.actions.PlaylistActions
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.presentation.navigation.Screen
import com.sosauce.cutemusic.presentation.screens.main.components.SortingDropdownMenu
import com.sosauce.cutemusic.presentation.screens.playlists.components.CreatePlaylistDialog
import com.sosauce.cutemusic.presentation.screens.playlists.components.PlaylistItem
import com.sosauce.cutemusic.presentation.shared_components.CuteSearchbar
import com.sosauce.cutemusic.presentation.shared_components.CuteText
import com.sosauce.cutemusic.presentation.shared_components.NoResult
import com.sosauce.cutemusic.presentation.shared_components.NoXFound
import com.sosauce.cutemusic.utils.ordered
import com.sosauce.cutemusic.utils.rememberSearchbarAlignment
import com.sosauce.cutemusic.utils.showCuteSearchbar
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PlaylistsScreen(
    musicState: MusicState,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerAction: (PlayerActions) -> Unit,
) {
    val context = LocalContext.current
    val playlistViewModel = koinViewModel<PlaylistViewModel>()
    val playlists by playlistViewModel.allPlaylists.collectAsStateWithLifecycle()
    var showPlaylistCreatorDialog by remember { mutableStateOf(false) }
    val state = rememberLazyListState()
    val textFieldState = rememberTextFieldState()
    var isSortedByASC by rememberSaveable { mutableStateOf(true) }
    var fabMenuExpanded by remember { mutableStateOf(false) }


    val importPlaylistLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
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
                        NoXFound(
                            headlineText = R.string.no_playlists,
                            bodyText = R.string.no_playlist_desc,
                            icon = R.drawable.playlist
                        )
                    }
                } else {

                    val orderedPlaylist = playlists.ordered(
                        sortAsc = isSortedByASC,
                        filterSelector = { it.name.contains(textFieldState.text, true) },
                        sortingSelector = { it.name.lowercase() }
                    )

                    if (orderedPlaylist.isEmpty()) {
                        item { NoResult() }
                    } else {
                        items(
                            items = orderedPlaylist,
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
            }

            AnimatedVisibility(
                visible = state.showCuteSearchbar,
                modifier = Modifier.align(rememberSearchbarAlignment()),
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                CuteSearchbar(
                    textFieldState = textFieldState,
                    musicState = musicState,
                    showSearchField = !state.isScrollInProgress,
                    sortingMenu = {
                        SortingDropdownMenu(
                            isSortedByASC = isSortedByASC,
                            onChangeSorting = { isSortedByASC = it },
                            sortingOptions = {}
                        )
                    },
                    onHandlePlayerActions = onHandlePlayerAction,
                    onNavigate = onNavigate,
                    fab = {
                        FloatingActionButtonMenu(
                            modifier = Modifier.offset(
                                12.dp,
                                12.dp
                            ), // Why they didn't make the padding optional is beyond me
                            expanded = fabMenuExpanded,
                            button = {
                                ToggleFloatingActionButton(
                                    checked = fabMenuExpanded,
                                    onCheckedChange = { fabMenuExpanded = !fabMenuExpanded },
                                    containerSize = { 45.dp }
                                ) {
                                    val imageVector by remember {
                                        derivedStateOf {
                                            if (checkedProgress > 0.5f) Icons.Rounded.Close else Icons.Rounded.Add
                                        }
                                    }

                                    Icon(
                                        imageVector = imageVector,
                                        contentDescription = null,
                                        modifier = Modifier.animateIcon({ checkedProgress })
                                    )
                                }
                            }
                        ) {
                            FloatingActionButtonMenuItem(
                                onClick = {
                                    importPlaylistLauncher.launch(arrayOf("*/*"))
                                    fabMenuExpanded = false
                                },
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.resource_import),
                                        contentDescription = null
                                    )
                                },
                                text = { CuteText(stringResource(R.string.import_playlist)) },
                            )
                            FloatingActionButtonMenuItem(
                                onClick = {
                                    showPlaylistCreatorDialog = true
                                    fabMenuExpanded = false
                                },
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.playlist_add),
                                        contentDescription = null
                                    )
                                },
                                text = { CuteText(stringResource(R.string.create_playlist)) },
                            )
                        }

                    }
                )
            }
        }
    }
}