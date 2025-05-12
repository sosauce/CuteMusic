@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.artist

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.MediaItemActions
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.domain.model.Artist
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.album.AlbumCard
import com.sosauce.cutemusic.ui.shared_components.CuteActionButton
import com.sosauce.cutemusic.ui.shared_components.CuteNavigationButton
import com.sosauce.cutemusic.ui.shared_components.CuteSearchbar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.LocalMusicListItem
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.shared_components.SelectedBar
import com.sosauce.cutemusic.utils.ICON_TEXT_SPACING
import com.sosauce.cutemusic.utils.SharedTransitionKeys
import com.sosauce.cutemusic.utils.rememberSearchbarAlignment
import com.sosauce.cutemusic.utils.showCuteSearchbar

@Composable
fun SharedTransitionScope.ArtistDetails(
    musics: List<MediaItem>,
    albums: List<Album>,
    artist: Artist,
    onNavigate: (Screen) -> Unit,
    onNavigateUp: () -> Unit,
    musicState: MusicState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onHandlePlayerAction: (PlayerActions) -> Unit,
    onHandleMediaItemAction: (MediaItemActions) -> Unit,
    onLoadMetadata: (String, Uri) -> Unit = { _, _ -> },
) {
    if (rememberIsLandscape()) {
        ArtistDetailsLandscape(
            musics = musics,
            albums = albums,
            onNavigateUp = onNavigateUp,
            onNavigate = onNavigate,
            artist = artist,
            currentMusicUri = musicState.uri,
            isPlayerReady = musicState.isPlayerReady,
            animatedVisibilityScope = animatedVisibilityScope,
            onLoadMetadata = onLoadMetadata,
            onHandlePlayerAction = onHandlePlayerAction,
            onHandleMediaItemAction = onHandleMediaItemAction
        )
    } else {
        val listState = rememberLazyListState()
        val selectedTracks = remember { mutableStateListOf<String>() }


        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing
            ) { paddingValues ->
                LazyColumn(
                    contentPadding = paddingValues,
                    state = listState
                ) {
                    item(
                        key = "artist details"
                    ) {
                        CuteText(
                            text = artist.name,
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier
                                .padding(start = 15.dp)
                                .sharedBounds(
                                    sharedContentState = rememberSharedContentState(key = artist.name + artist.id),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                )
                        )
                        Spacer(Modifier.height(20.dp))
                        if (albums.isNotEmpty()) {
                            CuteText(
                                text = pluralStringResource(
                                    R.plurals.albums,
                                    albums.size,
                                    albums.size
                                ),
                                modifier = Modifier.padding(start = 20.dp)
                            )
                        }
                    }
                    item(
                        key = "artist albums"
                    ) {
                        LazyRow {
                            items(
                                items = albums,
                                key = { it.id }
                            ) { album ->
                                AlbumCard(
                                    album = album,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(15.dp))
                                        .clickable { onNavigate(Screen.AlbumsDetails(album.id)) },
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CuteText(
                                text = pluralStringResource(
                                    R.plurals.tracks,
                                    musics.size,
                                    musics.size
                                ),
                                modifier = Modifier.padding(start = 10.dp)
                            )
                            IconButton(
                                onClick = { selectedTracks.addAll(musics.map { it.mediaId }) }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.PlaylistAdd,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                    items(
                        items = musics,
                        key = { it.mediaId }
                    ) { music ->
                        LocalMusicListItem(
                            music = music,
                            onShortClick = { mediaId ->
                                if (selectedTracks.isEmpty()) {
                                    onHandlePlayerAction(
                                        PlayerActions.StartArtistPlayback(
                                            artistName = artist.name,
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
                            currentMusicUri = musicState.uri,
                            isPlayerReady = musicState.isPlayerReady,
                            onLoadMetadata = onLoadMetadata,
                            onNavigate = onNavigate,
                            onHandleMediaItemAction = onHandleMediaItemAction,
                            isSelected = selectedTracks.contains(music.mediaId)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = listState.showCuteSearchbar,
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
                            currentlyPlaying = musicState.title,
                            isPlayerReady = musicState.isPlayerReady,
                            isPlaying = musicState.isPlaying,
                            onHandlePlayerActions = onHandlePlayerAction,
                            animatedVisibilityScope = animatedVisibilityScope,
                            showSearchField = false,
                            onNavigate = onNavigate,
                            fab = {
                                CuteActionButton(
                                    modifier = Modifier.sharedBounds(
                                        sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.FAB),
                                        animatedVisibilityScope = animatedVisibilityScope
                                    )
                                ) {
                                    onHandlePlayerAction(
                                        PlayerActions.StartArtistPlayback(
                                            artistName = artist.name,
                                            mediaId = null
                                        )
                                    )
                                }
                            },
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
}