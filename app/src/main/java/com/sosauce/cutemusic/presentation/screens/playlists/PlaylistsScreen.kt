@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.playlists

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberPlaylistSort
import com.sosauce.cutemusic.data.models.Playlist
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.domain.actions.PlaylistActions
import com.sosauce.cutemusic.presentation.navigation.Screen
import com.sosauce.cutemusic.presentation.screens.main.components.SortingDropdownMenu
import com.sosauce.cutemusic.presentation.screens.playlists.components.CreatePlaylistDialog
import com.sosauce.cutemusic.presentation.screens.playlists.components.PlaylistItem
import com.sosauce.cutemusic.presentation.shared_components.CuteDropdownMenuItem
import com.sosauce.cutemusic.presentation.shared_components.CuteSearchbar
import com.sosauce.cutemusic.presentation.shared_components.NoResult
import com.sosauce.cutemusic.presentation.shared_components.NoXFound
import com.sosauce.cutemusic.utils.PlaylistSort
import com.sosauce.cutemusic.utils.ordered
import com.sosauce.cutemusic.utils.selfAlignHorizontally

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PlaylistsScreen(
    playlists: List<Playlist>,
    onHandlePlaylistAction: (PlaylistActions) -> Unit,
    musicState: MusicState,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerAction: (PlayerActions) -> Unit,
) {
    val context = LocalContext.current
    val resources = LocalResources.current
    var showPlaylistCreatorDialog by remember { mutableStateOf(false) }
    val state = rememberLazyListState()
    val textFieldState = rememberTextFieldState()
    var isSortedByASC by rememberSaveable { mutableStateOf(true) }
    var fabMenuExpanded by remember { mutableStateOf(false) }
    var playlistSort by rememberPlaylistSort()


    val importPlaylistLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                if (!it.toString().endsWith(".m3u")) {
                    Toast.makeText(
                        context,
                        resources.getString(R.string.not_m3u_file),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    onHandlePlaylistAction(PlaylistActions.ImportM3uPlaylist(it))
                }
            }

        }

    if (showPlaylistCreatorDialog) {
        CreatePlaylistDialog { showPlaylistCreatorDialog = false }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            CuteSearchbar(
                modifier = Modifier.selfAlignHorizontally(),
                textFieldState = textFieldState,
                musicState = musicState,
                sortingMenu = {
                    SortingDropdownMenu(
                        isSortedByASC = isSortedByASC,
                        onChangeSorting = { isSortedByASC = it },
                        sortingOptions = {
                            CuteDropdownMenuItem(
                                onClick = { playlistSort = 0 },
                                text = { Text(stringResource(R.string.name)) },
                                leadingIcon = {
                                    RadioButton(
                                        selected = playlistSort == 0,
                                        onClick = null
                                    )
                                }
                            )
                            CuteDropdownMenuItem(
                                onClick = { playlistSort = 1 },
                                text = { Text("Number of tracks") },
                                leadingIcon = {
                                    RadioButton(
                                        selected = playlistSort == 1,
                                        onClick = null
                                    )
                                }
                            )
                            CuteDropdownMenuItem(
                                onClick = { playlistSort = 2 },
                                text = { Text("Tags") },
                                leadingIcon = {
                                    RadioButton(
                                        selected = playlistSort == 2,
                                        onClick = null
                                    )
                                }
                            )
                            CuteDropdownMenuItem(
                                onClick = { playlistSort = 3 },
                                text = { Text("Color") },
                                leadingIcon = {
                                    RadioButton(
                                        selected = playlistSort == 3,
                                        onClick = null
                                    )
                                }
                            )
                        }
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
                                containerSize = { 56.dp }
                            ) {
                                val icon by remember {
                                    derivedStateOf {
                                        if (checkedProgress > 0.5f) R.drawable.close else R.drawable.add
                                    }
                                }

                                Icon(
                                    painter = painterResource(icon),
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
                            text = { Text(stringResource(R.string.import_playlist)) },
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
                            text = { Text(stringResource(R.string.create_playlist)) },
                        )
                    }

                }
            )
        }
    ) { paddingValues ->
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
                    sort = PlaylistSort.entries[playlistSort],
                    ascending = isSortedByASC,
                    query = textFieldState.text.toString()
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
                            onHandlePlaylistActions = onHandlePlaylistAction,
                            onClickPlaylist = { onNavigate(Screen.PlaylistDetails(playlist.id)) }
                        )
                    }
                }
            }
        }
    }
}