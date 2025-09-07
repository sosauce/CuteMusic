@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.cutemusic.presentation.screens.artist

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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.MediaItemActions
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.domain.model.Artist
import com.sosauce.cutemusic.presentation.navigation.Screen
import com.sosauce.cutemusic.presentation.screens.album.components.NumberOfTracks
import com.sosauce.cutemusic.presentation.screens.artist.components.ArtistHeader
import com.sosauce.cutemusic.presentation.screens.artist.components.ArtistHeaderLandscape
import com.sosauce.cutemusic.presentation.screens.artist.components.NumberOfAlbums
import com.sosauce.cutemusic.presentation.shared_components.CuteNavigationButton
import com.sosauce.cutemusic.presentation.shared_components.CuteSearchbar
import com.sosauce.cutemusic.presentation.shared_components.CuteText
import com.sosauce.cutemusic.presentation.shared_components.LocalMusicListItem
import com.sosauce.cutemusic.presentation.shared_components.SelectedBar
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.rememberSearchbarAlignment
import com.sosauce.cutemusic.utils.showCuteSearchbar

@Composable
fun SharedTransitionScope.ArtistDetailsScreen(
    musics: List<MediaItem>,
    albums: List<Album>,
    artist: Artist,
    onNavigate: (Screen) -> Unit,
    onNavigateUp: () -> Unit,
    musicState: MusicState,
    onHandlePlayerAction: (PlayerActions) -> Unit,
    onHandleMediaItemAction: (MediaItemActions) -> Unit,
    onLoadMetadata: (String, Uri) -> Unit,
) {
    val context = LocalContext.current
    val state = rememberLazyListState()
    val isLandscape = rememberIsLandscape()
    val selectedTracks = remember { mutableStateListOf<String>() }

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
                        ArtistHeaderLandscape(
                            artist = artist,
                            musics = musics,
                            onHandlePlayerActions = onHandlePlayerAction
                        )
                    } else {
                        ArtistHeader(
                            artist = artist,
                            musics = musics,
                            onHandlePlayerActions = onHandlePlayerAction
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                }

                if (albums.isNotEmpty()) {
                    item(
                        key = "Albums"
                    ) {
                        NumberOfAlbums(artist.numberAlbums)

                        HorizontalMultiBrowseCarousel(
                            state = rememberCarouselState { albums.count() },
                            preferredItemWidth = 186.dp,
                            itemSpacing = 8.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 16.dp, bottom = 16.dp)
                        ) { index ->
                            val album = albums[index]

                            Box(
                                modifier = Modifier
                                    .height(200.dp)
                                    .sharedElement(
                                        sharedContentState = rememberSharedContentState(key = album.id),
                                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                                    )
                                    .maskClip(MaterialTheme.shapes.extraLarge)
                                    .clickable { onNavigate(Screen.AlbumsDetails(album.id)) },
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                AsyncImage(
                                    model = ImageUtils.imageRequester(
                                        ImageUtils.getAlbumArt(album.id)
                                            ?: androidx.media3.session.R.drawable.media3_icon_album,
                                        context
                                    ),
                                    contentDescription = stringResource(R.string.artwork),
                                    contentScale = ContentScale.Crop
                                )
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CuteText(
                                        text = album.name,
                                        style = MaterialTheme.typography.headlineSmallEmphasized,
                                        modifier = Modifier.dropShadow(
                                            shape = RoundedCornerShape(10.dp),
                                            shadow = Shadow(20.dp)
                                        )
                                    )
                                    CuteText(
                                        text = album.artist,
                                        style = MaterialTheme.typography.bodyMediumEmphasized.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        modifier = Modifier.dropShadow(
                                            shape = RoundedCornerShape(10.dp),
                                            shadow = Shadow(20.dp)
                                        )
                                    )
                                }
                            }

                        }

//                        LazyRow {
//                            items(
//                                items = albums,
//                                key = { it.id }
//                            ) { album ->
//                                AlbumCard(
//                                    album = album,
//                                    onClick = { onNavigate(Screen.AlbumsDetails(album.id)) }
//                                )
//                            }
//                        }
                    }
                }

                if (musics.isNotEmpty()) {
                    item(
                        key = "NbTracks"
                    ) {
                        NumberOfTracks(
                            size = musics.size,
                            onAddToSelected = { selectedTracks.addAll(musics.map { it.mediaId }) }
                        )
                    }

                    items(
                        items = musics,
                        key = { it.mediaId }
                    ) { music ->
                        LocalMusicListItem(
                            modifier = Modifier.animateItem(),
                            music = music,
                            musicState = musicState,
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
                            onLoadMetadata = onLoadMetadata,
                            onNavigate = onNavigate,
                            onHandleMediaItemAction = onHandleMediaItemAction,
                            isSelected = selectedTracks.contains(music.mediaId),
                            onHandlePlayerActions = onHandlePlayerAction
                        )
                    }
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
                        onHandlePlayerActions = onHandlePlayerAction,
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