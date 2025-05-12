@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.album

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
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.PlaylistAdd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.shared_components.CuteActionButton
import com.sosauce.cutemusic.ui.shared_components.CuteNavigationButton
import com.sosauce.cutemusic.ui.shared_components.CuteSearchbar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.LocalMusicListItem
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.SharedTransitionKeys
import com.sosauce.cutemusic.utils.cuteHazeEffect
import com.sosauce.cutemusic.utils.rememberHazeState
import com.sosauce.cutemusic.utils.rememberSearchbarAlignment
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.hazeSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.modifier.ModifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.util.fastFilter
import com.sosauce.cutemusic.data.actions.MediaItemActions
import com.sosauce.cutemusic.ui.shared_components.SelectedBar
import com.sosauce.cutemusic.utils.ICON_TEXT_SPACING
import com.sosauce.cutemusic.utils.showCuteSearchbar

@Composable
fun SharedTransitionScope.AlbumDetailsScreen(
    musics: List<MediaItem>,
    album: Album,
    onNavigateUp: () -> Unit,
    musicState: MusicState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onHandleMediaItemAction: (MediaItemActions) -> Unit,
    onNavigate: (Screen) -> Unit,
    onLoadMetadata: (String, Uri) -> Unit = { _, _ -> },
) {

    if (rememberIsLandscape()) {
        AlbumDetailsLandscape(
            musics = musics,
            album = album,
            onNavigateUp = onNavigateUp,
            musicState = musicState,
            animatedVisibilityScope = animatedVisibilityScope,
            onLoadMetadata = onLoadMetadata,
            onNavigate = onNavigate,
            onHandlePlayerActions = onHandlePlayerActions,
            onHandleMediaItemAction = onHandleMediaItemAction
        )
    } else {
        AlbumDetailsContent(
            musics = musics,
            album = album,
            onNavigateUp = onNavigateUp,
            musicState = musicState,
            animatedVisibilityScope = animatedVisibilityScope,
            onNavigate = onNavigate,
            onLoadMetadata = onLoadMetadata,
            onHandlePlayerActions = onHandlePlayerActions,
            onHandleMediaItemAction = onHandleMediaItemAction
        )
    }

}

@Composable
private fun SharedTransitionScope.AlbumDetailsContent(
    musics: List<MediaItem>,
    album: Album,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onHandleMediaItemAction: (MediaItemActions) -> Unit,
    onNavigateUp: () -> Unit,
    musicState: MusicState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigate: (Screen) -> Unit,
    onLoadMetadata: (String, Uri) -> Unit = { _, _ -> },
) {
    val selectedTracks = remember { mutableStateListOf<String>() }
    val state = rememberLazyListState()

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
                item {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Top
                    ) {
                        AsyncImage(
                            model = ImageUtils.imageRequester(ImageUtils.getAlbumArt(album.id)),
                            contentDescription = stringResource(R.string.artwork),
                            modifier = Modifier
                                .size(150.dp)
                                .sharedElement(
                                    sharedContentState = rememberSharedContentState(key = album.id),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                )
                                .clip(RoundedCornerShape(24.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(
                            horizontalAlignment = Alignment.Start
                        ) {
                            CuteText(
                                text = album.name,
                                fontSize = 22.sp,
                                modifier = Modifier
                                    .sharedElement(
                                        sharedContentState = rememberSharedContentState(key = album.name + album.id),
                                        animatedVisibilityScope = animatedVisibilityScope,
                                    )
                                    .basicMarquee()
                            )
                            CuteText(
                                text = album.artist,
                                fontSize = 22.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                                modifier = Modifier
                                    .sharedElement(
                                        sharedContentState = rememberSharedContentState(key = album.artist + album.id),
                                        animatedVisibilityScope = animatedVisibilityScope,
                                    )
                                    .basicMarquee()
                            )
                            OutlinedButton(
                                onClick = { selectedTracks.addAll(musics.map { it.mediaId }) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.PlaylistAdd,
                                        contentDescription = null
                                    )
                                    Spacer(Modifier.width(ICON_TEXT_SPACING.dp))
                                    CuteText(stringResource(R.string.add_to_playlist))
                                }
                            }

                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    CuteText(
                        text = pluralStringResource(
                            R.plurals.tracks,
                            musics.size,
                            musics.size
                        ),
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

                items(
                    items = musics
                        .sortedWith(
                            compareBy(
                                { it.mediaMetadata.trackNumber == null || it.mediaMetadata.trackNumber == 0 },
                                { it.mediaMetadata.trackNumber }
                            )
                        ),
                    key = { it.mediaId }
                ) { music ->
                    LocalMusicListItem(
                        music = music,
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
                        currentMusicUri = musicState.uri,
                        isPlayerReady = musicState.isPlayerReady,
                        onLoadMetadata = onLoadMetadata,
                        onHandleMediaItemAction = onHandleMediaItemAction,
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
                        currentlyPlaying = musicState.title,
                        isPlayerReady = musicState.isPlayerReady,
                        isPlaying = musicState.isPlaying,
                        onHandlePlayerActions = onHandlePlayerActions,
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
                                onHandlePlayerActions(
                                    PlayerActions.StartAlbumPlayback(
                                        albumName = album.name,
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
