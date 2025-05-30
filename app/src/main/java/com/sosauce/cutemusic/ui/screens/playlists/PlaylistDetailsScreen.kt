@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.playlists

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sosauce.cutemusic.data.actions.MediaItemActions
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.actions.PlaylistActions
import com.sosauce.cutemusic.data.datastore.rememberAllSafTracks
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.model.Playlist
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.shared_components.CuteActionButton
import com.sosauce.cutemusic.ui.shared_components.CuteNavigationButton
import com.sosauce.cutemusic.ui.shared_components.CuteSearchbar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.LocalMusicListItem
import com.sosauce.cutemusic.ui.shared_components.RemoveFromPlaylistDropdownItem
import com.sosauce.cutemusic.ui.shared_components.SafMusicListItem
import com.sosauce.cutemusic.utils.ICON_TEXT_SPACING
import com.sosauce.cutemusic.utils.SharedTransitionKeys
import com.sosauce.cutemusic.utils.copyMutate
import com.sosauce.cutemusic.utils.rememberSearchbarAlignment
import com.sosauce.cutemusic.utils.showCuteSearchbar

@Composable
fun SharedTransitionScope.PlaylistDetailsScreen(
    playlist: Playlist,
    musicState: MusicState,
    musics: List<MediaItem>,
    onNavigate: (Screen) -> Unit,
    onLoadMetadata: (String, Uri) -> Unit = {_, _ ->},
    onHandlePlayerAction: (PlayerActions) -> Unit,
    onHandlePlaylistAction: (PlaylistActions) -> Unit,
    isPlayerReady: Boolean,
    currentMusicUri: String,
    onNavigateUp: () -> Unit,
    onHandleMediaItemAction: (MediaItemActions) -> Unit,
) {

    val playlistDisplay = remember {
        if (playlist.emoji.isNotBlank()) {
            "${playlist.emoji} ${playlist.name}"
        } else {
            playlist.name
        }
    }

    val playlistMusic = remember(playlist.musics) {
        val idSet = playlist.musics.toSet() // O(1) or do we just lose time/performance converting?
        musics
            .filter { it.mediaId in idSet }
            .sortedBy { it.mediaMetadata.title.toString() }
    }

    val listState = rememberLazyListState()

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = paddingValues,
                state = listState
            ) {
                items(
                    items = playlistMusic,
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
                        if (music.mediaMetadata.extras?.getBoolean("is_saf") == false) {
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
                                onNavigate = { onNavigate(it) },
                                currentMusicUri = currentMusicUri,
                                onLoadMetadata = onLoadMetadata,
                                isPlayerReady = isPlayerReady,
                                onHandleMediaItemAction = onHandleMediaItemAction,
                                onHandlePlayerActions = onHandlePlayerAction,
                                playlistDropdownMenuItem = {
                                    RemoveFromPlaylistDropdownItem(
                                        onRemoveFromPlaylist = {
                                            val playlist = Playlist(
                                                id = playlist.id,
                                                emoji = playlist.emoji,
                                                name = playlist.name,
                                                musics = playlist.musics.copyMutate { remove(music.mediaId) }
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
                                currentMusicUri = currentMusicUri,
                                isPlayerReady = isPlayerReady,
                                onDeleteFromSaf = {
                                    safTracks = safTracks.copyMutate { remove(music.mediaMetadata.extras?.getString("uri")) }
                                },
                                playlistDropdownMenuItem = {
                                    RemoveFromPlaylistDropdownItem(
                                        onRemoveFromPlaylist = {
                                            val playlist = Playlist(
                                                emoji = playlist.emoji,
                                                name = playlist.name,
                                                musics = playlist.musics.copyMutate { remove(music.mediaId) }
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

            AnimatedVisibility(
                visible = listState.showCuteSearchbar,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
                modifier = Modifier.align(rememberSearchbarAlignment()),
            ) {
                CuteSearchbar(
                    currentlyPlaying = musicState.title,
                    isPlayerReady = musicState.isPlayerReady,
                    isPlaying = musicState.isPlaying,
                    onHandlePlayerActions = onHandlePlayerAction,
                    showSearchField = false,
                    onNavigate = onNavigate,
                    fab = {
                        CuteActionButton(
                            modifier = Modifier.sharedBounds(
                                sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.FAB),
                                animatedVisibilityScope = LocalNavAnimatedContentScope.current
                            )
                        ) {
                            onHandlePlayerAction(
                                PlayerActions.StartPlaylistPlayback(
                                    playlist.musics,
                                    null
                                )
                            )
                        }
                    },
                    navigationIcon = {
                        CuteNavigationButton(
                            modifier = Modifier
                                .padding(start = 15.dp)
                                .navigationBarsPadding(),
                            playlistName = {
                                Spacer(Modifier.width(ICON_TEXT_SPACING.dp))
                                CuteText(playlistDisplay)
                            }
                        ) { onNavigateUp() }
                    }
                )
            }

        }
    }

}