@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.playlists

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import com.sosauce.cutemusic.data.datastore.rememberAllSafTracks
import com.sosauce.cutemusic.domain.model.Playlist
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.shared_components.CuteNavigationButton
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.LocalMusicListItem
import com.sosauce.cutemusic.ui.shared_components.SafMusicListItem

@Composable
fun SharedTransitionScope.PlaylistDetailsScreen(
    playlist: Playlist,
    musics: List<MediaItem>,
    onNavigate: (Screen) -> Unit,
    onShortClick: (String) -> Unit,
    onLoadMetadata: (String, Uri) -> Unit = { _, _ -> },
    isPlayerReady: Boolean,
    currentMusicUri: String,
    onDeleteMusic: (List<Uri>, ActivityResultLauncher<IntentSenderRequest>) -> Unit,
    onChargeAlbumSongs: (String) -> Unit,
    onChargeArtistLists: (String) -> Unit,
    onNavigateUp: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {

    val playlistDisplay = remember {
        if (playlist.emoji.isNotBlank()) {
            "${playlist.emoji} ${playlist.name}"
        } else {
            playlist.name
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = paddingValues
            ) {
                items(
                    items = playlist.musics,
                    key = { it.hashCode() }
                ) { id ->
                    musics.find { it.mediaId == id }?.let { music ->
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
                                    onShortClick = { onShortClick(music.mediaId) },
                                    music = music,
                                    onNavigate = { onNavigate(it) },
                                    currentMusicUri = currentMusicUri,
                                    onLoadMetadata = onLoadMetadata,
                                    showBottomSheet = true,
                                    onDeleteMusic = onDeleteMusic,
                                    onChargeAlbumSongs = onChargeAlbumSongs,
                                    onChargeArtistLists = onChargeArtistLists,
                                    isPlayerReady = isPlayerReady,
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                            } else {
                                var safTracks by rememberAllSafTracks()
                                SafMusicListItem(
                                    onShortClick = { onShortClick(music.mediaId) },
                                    music = music,
                                    currentMusicUri = currentMusicUri,
                                    showBottomSheet = true,
                                    isPlayerReady = isPlayerReady,
                                    onDeleteFromSaf = {
                                        safTracks = safTracks.toMutableSet().apply {
                                            remove(music.mediaMetadata.extras?.getString("uri"))
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            CuteNavigationButton(
                modifier = Modifier
                    .padding(start = 15.dp)
                    .navigationBarsPadding()
                    .align(Alignment.BottomStart),
                playlistName = {
                    Spacer(Modifier.width(5.dp))
                    CuteText(playlistDisplay)
                }
            ) { onNavigateUp() }
        }
    }

}