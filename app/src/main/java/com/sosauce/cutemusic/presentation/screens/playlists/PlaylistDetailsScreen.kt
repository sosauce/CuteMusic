@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.presentation.screens.playlists

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.navigation3.runtime.NavKey
import com.sosauce.cutemusic.data.actions.MediaItemActions
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.actions.PlaylistActions
import com.sosauce.cutemusic.data.datastore.rememberAllSafTracks
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.model.Playlist
import com.sosauce.cutemusic.presentation.navigation.Screen
import com.sosauce.cutemusic.presentation.screens.album.components.NumberOfTracks
import com.sosauce.cutemusic.presentation.screens.playlists.components.EmptyPlaylist
import com.sosauce.cutemusic.presentation.screens.playlists.components.PlaylistHeader
import com.sosauce.cutemusic.presentation.screens.playlists.components.PlaylistHeaderLandscape
import com.sosauce.cutemusic.presentation.shared_components.CuteNavigationButton
import com.sosauce.cutemusic.presentation.shared_components.CuteSearchbar
import com.sosauce.cutemusic.presentation.shared_components.LocalMusicListItem
import com.sosauce.cutemusic.presentation.shared_components.RemoveFromPlaylistDropdownItem
import com.sosauce.cutemusic.presentation.shared_components.SafMusicListItem
import com.sosauce.cutemusic.utils.comesFromSaf
import com.sosauce.cutemusic.utils.copyMutate
import com.sosauce.cutemusic.utils.rememberSearchbarAlignment
import com.sosauce.cutemusic.utils.showCuteSearchbar

@Composable
fun SharedTransitionScope.PlaylistDetailsScreen(
    playlist: Playlist,
    musicState: MusicState,
    musics: List<MediaItem>,
    onNavigate: (Screen) -> Unit,
    onLoadMetadata: (String, Uri) -> Unit,
    onHandlePlayerAction: (PlayerActions) -> Unit,
    onHandlePlaylistAction: (PlaylistActions) -> Unit,
    onNavigateUp: () -> Unit,
    onHandleMediaItemAction: (MediaItemActions) -> Unit,
) {

    val listState = rememberLazyListState()
    val isLandscape = rememberIsLandscape()

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = paddingValues,
                state = listState
            ) {

                if (musics.isEmpty()) {
                    item(
                        "EmptyHeader"
                    ) {
                        EmptyPlaylist(playlist.emoji)
                    }
                } else {


                    item(
                        "Header"
                    ) {
                        if (isLandscape) {
                            PlaylistHeaderLandscape(
                                playlist = playlist,
                                musics = musics,
                                onHandlePlayerActions = onHandlePlayerAction
                            )
                        } else {
                            PlaylistHeader(
                                playlist = playlist,
                                musics = musics,
                                onHandlePlayerActions = onHandlePlayerAction
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        NumberOfTracks(size = musics.size)
                    }

                    items(
                        items = musics,
                        key = { it.mediaId }
                    ) { music ->
                        Column(
                            modifier = Modifier
                                .animateItem()
                                .padding(
                                    vertical = 2.dp,
                                    horizontal = 4.dp
                                )
                        ) {
                            if (music.comesFromSaf) {
                                LocalMusicListItem(
                                    onShortClick = {
                                        onHandlePlayerAction(
                                            PlayerActions.StartPlaylistPlayback(
                                                playlist.musics,
                                                music.mediaId
                                            )
                                        )
                                    },
                                    music = music,
                                    musicState = musicState,
                                    onNavigate = { onNavigate(it) },
                                    onLoadMetadata = onLoadMetadata,
                                    onHandleMediaItemAction = onHandleMediaItemAction,
                                    onHandlePlayerActions = onHandlePlayerAction,
                                    playlistDropdownMenuItem = {
                                        RemoveFromPlaylistDropdownItem(
                                            onRemoveFromPlaylist = {
                                                val playlist = Playlist(
                                                    id = playlist.id,
                                                    emoji = playlist.emoji,
                                                    name = playlist.name,
                                                    musics = playlist.musics.copyMutate {
                                                        remove(
                                                            music.mediaId
                                                        )
                                                    }
                                                )

                                                onHandlePlaylistAction(
                                                    PlaylistActions.UpsertPlaylist(playlist)
                                                )
                                            }
                                        )
                                    }
                                )
                            } else {
                                var safTracks by rememberAllSafTracks()
                                SafMusicListItem(
                                    onShortClick = {
                                        onHandlePlayerAction(
                                            PlayerActions.StartPlaylistPlayback(
                                                playlist.musics,
                                                music.mediaId
                                            )
                                        )
                                    },
                                    music = music,
                                    currentMusicUri = musicState.uri,
                                    isPlayerReady = musicState.isPlayerReady,
                                    onDeleteFromSaf = {
                                        safTracks = safTracks.copyMutate {
                                            remove(
                                                music.mediaMetadata.extras?.getString("uri")
                                            )
                                        }
                                    },
                                    playlistDropdownMenuItem = {
                                        RemoveFromPlaylistDropdownItem(
                                            onRemoveFromPlaylist = {
                                                val playlist = Playlist(
                                                    emoji = playlist.emoji,
                                                    name = playlist.name,
                                                    musics = playlist.musics.copyMutate {
                                                        remove(
                                                            music.mediaId
                                                        )
                                                    }
                                                )

                                                onHandlePlaylistAction(
                                                    PlaylistActions.UpsertPlaylist(playlist)
                                                )
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

            }

            AnimatedVisibility(
                visible = listState.showCuteSearchbar,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
                modifier = Modifier.align(rememberSearchbarAlignment()),
            ) {
                CuteSearchbar(
                    onHandlePlayerActions = onHandlePlayerAction,
                    musicState = musicState,
                    showSearchField = false,
                    onNavigate = onNavigate,
                    navigationIcon = {
                        CuteNavigationButton(
                            modifier = Modifier
                                .padding(start = 15.dp)
                                .navigationBarsPadding()
                        ) { onNavigateUp() }
                    }
                )
            }

        }
    }

}