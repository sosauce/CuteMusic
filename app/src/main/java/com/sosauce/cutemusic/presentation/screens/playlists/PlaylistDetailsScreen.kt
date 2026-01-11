@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.playlists

import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import com.materialkolor.DynamicMaterialExpressiveTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicMaterialThemeState
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberAppTheme
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.data.datastore.rememberTrackSort
import com.sosauce.cutemusic.data.datastore.rememberUseExpressivePalette
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.domain.actions.PlaylistActions
import com.sosauce.cutemusic.presentation.multiselect.rememberMultiSelectState
import com.sosauce.cutemusic.presentation.navigation.Screen
import com.sosauce.cutemusic.presentation.screens.album.components.NumberOfTracks
import com.sosauce.cutemusic.presentation.screens.playlists.components.EmptyPlaylist
import com.sosauce.cutemusic.presentation.screens.playlists.components.PlaylistHeader
import com.sosauce.cutemusic.presentation.screens.playlists.components.PlaylistHeaderLandscape
import com.sosauce.cutemusic.presentation.screens.playlists.components.PlaylistPicker
import com.sosauce.cutemusic.presentation.shared_components.CuteSearchbar
import com.sosauce.cutemusic.presentation.shared_components.MoreOptions
import com.sosauce.cutemusic.presentation.shared_components.MusicListItem
import com.sosauce.cutemusic.presentation.shared_components.SelectedBar
import com.sosauce.cutemusic.presentation.shared_components.SortingDropdownMenu
import com.sosauce.cutemusic.utils.CuteTheme
import com.sosauce.cutemusic.utils.copyMutate
import com.sosauce.cutemusic.utils.selfAlignHorizontally

@Composable
fun SharedTransitionScope.PlaylistDetailsScreen(
    state: PlaylistDetailsState,
    musicState: MusicState,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerAction: (PlayerActions) -> Unit,
    onHandlePlaylistAction: (PlaylistActions) -> Unit,
    onNavigateUp: () -> Unit
) {

    val context = LocalContext.current
    val listState = rememberLazyListState()
    val isLandscape = rememberIsLandscape()
    val theme by rememberAppTheme()
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val useExpressivePalette by rememberUseExpressivePalette()
    var sortTracksAsc by rememberSaveable { mutableStateOf(true) }
    var trackSort by rememberTrackSort()
    val multiSelectState = rememberMultiSelectState<CuteTrack>()




    DynamicMaterialExpressiveTheme(
        state = rememberDynamicMaterialThemeState(
            seedColor = state.playlist.color.takeIf { it != -1 }?.let { Color(it) }
                ?: MaterialTheme.colorScheme.primary,
            isDark = if (theme == CuteTheme.SYSTEM) isSystemInDarkTheme else if (theme == CuteTheme.AMOLED) true else theme == CuteTheme.DARK,
            isAmoled = theme == CuteTheme.AMOLED,
            specVersion = ColorSpec.SpecVersion.SPEC_2025,
            style = if (useExpressivePalette) PaletteStyle.Expressive else PaletteStyle.Fidelity
        )
    ) {
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
                            SelectedBar(
                                modifier = Modifier.selfAlignHorizontally(),
                                multiSelectState = multiSelectState,
                                items = state.tracks,
                                onToggleAll = {
                                    if (multiSelectState.selectedItems.size == state.tracks.size) {
                                        multiSelectState.clearSelected()
                                    } else {
                                        multiSelectState.toggleAll(state.tracks)
                                    }
                                }
                            ) {
                                var showPlaylistDialog by remember { mutableStateOf(false) }
                                val deleteSongLauncher =
                                    rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {}

                                if (showPlaylistDialog) {
                                    PlaylistPicker(
                                        mediaId = multiSelectState.selectedItems.fastMap { it.mediaId },
                                        onDismissRequest = { showPlaylistDialog = false },
                                        onAddingFinished = multiSelectState::clearSelected
                                    )
                                }

                                IconButton(
                                    onClick = { showPlaylistDialog = true },
                                    shapes = IconButtonDefaults.shapes()
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.playlist_add),
                                        contentDescription = null
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                            val intentSender = MediaStore.createDeleteRequest(
                                                context.contentResolver,
                                                multiSelectState.selectedItems.fastMap { it.uri }
                                            ).intentSender

                                            deleteSongLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                                        } else {
                                            multiSelectState.selectedItems.fastForEach {
                                                context.contentResolver.delete(it.uri, null, null)
                                            }
                                        }
                                    },
                                    shapes = IconButtonDefaults.shapes(),
                                    colors = IconButtonDefaults.iconButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.trash_rounded_filled),
                                        contentDescription = null
                                    )
                                }
                            }
                        } else {
                            CuteSearchbar(
                                onHandlePlayerActions = onHandlePlayerAction,
                                musicState = musicState,
                                showSearchField = false,
                                onNavigate = onNavigate,
                                onNavigateUp = onNavigateUp,
                                modifier = Modifier.selfAlignHorizontally()
                            )
                        }
                    }
                }
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 5.dp),
                    contentPadding = paddingValues,
                    state = listState
                ) {
                    if (state.tracks.isEmpty()) {
                        item(
                            "EmptyHeader"
                        ) {
                            EmptyPlaylist(state.playlist.emoji)
                        }
                    } else {
                        item(
                            "Header"
                        ) {
                            if (isLandscape) {
                                PlaylistHeaderLandscape(
                                    playlist = state.playlist,
                                    tracks = state.tracks,
                                    onHandlePlayerActions = onHandlePlayerAction
                                )
                            } else {
                                PlaylistHeader(
                                    playlist = state.playlist,
                                    tracks = state.tracks,
                                    onHandlePlayerActions = onHandlePlayerAction
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
                                modifier = Modifier
                                    .animateItem(),
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
                                isSelected = isSelected,
                                onLongClick = { multiSelectState.toggle(music) },
                                music = music,
                                musicState = musicState,
                                onNavigate = { onNavigate(it) },
                                onHandlePlayerActions = onHandlePlayerAction,
                                extraOptions = listOf(
                                    MoreOptions(
                                        text = { stringResource(R.string.remove_from_playlist) },
                                        icon = R.drawable.playlist_remove,
                                        onClick = {
                                            onHandlePlaylistAction(
                                                PlaylistActions.UpsertPlaylist(
                                                    state.playlist.copy(
                                                        musics = state.playlist.musics.copyMutate {
                                                            remove(
                                                                music.mediaId
                                                            )
                                                        }
                                                    )
                                                )
                                            )
                                        }
                                    )
                                )
                            )
                        }
                    }

                }
            }
        }
    }


}