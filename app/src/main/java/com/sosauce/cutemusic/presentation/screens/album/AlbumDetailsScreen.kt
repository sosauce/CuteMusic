@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.cutemusic.presentation.screens.album

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import com.sosauce.cutemusic.data.actions.MediaItemActions
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.presentation.navigation.Screen
import com.sosauce.cutemusic.presentation.screens.album.components.AlbumHeader
import com.sosauce.cutemusic.presentation.screens.album.components.AlbumHeaderLandscape
import com.sosauce.cutemusic.presentation.screens.album.components.NumberOfTracks
import com.sosauce.cutemusic.presentation.shared_components.CuteNavigationButton
import com.sosauce.cutemusic.presentation.shared_components.CuteSearchbar
import com.sosauce.cutemusic.presentation.shared_components.LocalMusicListItem
import com.sosauce.cutemusic.presentation.shared_components.SelectedBar
import com.sosauce.cutemusic.utils.rememberSearchbarAlignment
import com.sosauce.cutemusic.utils.showCuteSearchbar

@Composable
fun SharedTransitionScope.AlbumDetailsScreen(
    musics: List<MediaItem>,
    album: Album,
    onNavigateUp: () -> Unit,
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onHandleMediaItemAction: (MediaItemActions) -> Unit,
    onNavigate: (Screen) -> Unit,
    onLoadMetadata: (String, Uri) -> Unit,
) {
    val state = rememberLazyListState()
    val isLandscape = rememberIsLandscape()
    val selectedTracks = remember { mutableStateListOf<String>() }
    val sortedMusic = remember(musics) {
        musics
            .sortedWith(
                compareBy(
                    { it.mediaMetadata.trackNumber == null || it.mediaMetadata.trackNumber == 0 },
                    { it.mediaMetadata.trackNumber }
                )
            )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing
        ) { paddingValues ->

            LazyColumn(
                state = state,
                contentPadding = paddingValues
            ) {
                item(
                    key = "Header"
                ) {
                    if (isLandscape) {
                        AlbumHeaderLandscape(
                            album = album,
                            musics = sortedMusic,
                            onHandlePlayerActions = onHandlePlayerActions
                        )
                    } else {
                        AlbumHeader(
                            album = album,
                            musics = sortedMusic,
                            onHandlePlayerActions = onHandlePlayerActions
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    NumberOfTracks(
                        size = musics.size,
                        onAddToSelected = { selectedTracks.addAll(musics.map { it.mediaId }) }
                    )
                }

                items(
                    items = sortedMusic,
                    key = { it.mediaId }
                ) { music ->
                    LocalMusicListItem(
                        modifier = Modifier.animateItem(),
                        music = music,
                        musicState = musicState,
                        onShortClick = { mediaId ->
                            if (selectedTracks.isEmpty()) {
                                onHandlePlayerActions(
                                    PlayerActions.StartAlbumPlayback(
                                        albumName = music.mediaMetadata.albumTitle.toString(),
                                        mediaId = mediaId
                                    )
                                )
                            } else {
                                if (selectedTracks.contains(mediaId)) {
                                    selectedTracks.remove(mediaId)
                                } else {
                                    selectedTracks.add(mediaId)
                                }
                            }
                        },
                        onLoadMetadata = onLoadMetadata,
                        onHandleMediaItemAction = onHandleMediaItemAction,
                        onHandlePlayerActions = onHandlePlayerActions,
                        onNavigate = onNavigate,
                        showTrackNumber = true,
                        isSelected = selectedTracks.contains(music.mediaId)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = state.showCuteSearchbar,
            enter = slideInVertically { it },
            exit = slideOutVertically { it },
            modifier = Modifier.align(rememberSearchbarAlignment()),
        ) {
            AnimatedContent(
                targetState = selectedTracks.isEmpty(),
                transitionSpec = { scaleIn() togetherWith scaleOut() }
            ) {
                if (it) {
                    CuteSearchbar(
                        musicState = musicState,
                        onHandlePlayerActions = onHandlePlayerActions,
                        showSearchField = false,
                        onNavigate = onNavigate,
                        navigationIcon = {
                            CuteNavigationButton(
                                onNavigateUp = onNavigateUp
                            )
                        }
                    )
                } else {
                    SelectedBar(
                        selectedElements = selectedTracks,
                        onClearSelected = selectedTracks::clear
                    )
                }
            }
        }
    }
}
