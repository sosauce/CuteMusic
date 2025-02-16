@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.artist

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.domain.model.Artist
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.album.AlbumCard
import com.sosauce.cutemusic.ui.screens.main.MusicListItem
import com.sosauce.cutemusic.ui.shared_components.CuteNavigationButton
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.utils.thenIf

@Composable
fun SharedTransitionScope.ArtistDetailsLandscape(
    onNavigateUp: () -> Unit,
    artistAlbums: List<Album>,
    artistSongs: List<MediaItem>,
    onClickPlay: (String) -> Unit,
    onNavigate: (Screen) -> Unit,
    chargePVMAlbumSongs: (String) -> Unit,
    artist: Artist,
    currentMusicUri: String,
    isPlayerReady: Boolean,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .displayCutoutPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn {
                item {
                    ArtistInfoCard(
                        artist = artist,
                        animatedVisibilityScope = animatedVisibilityScope,
                        numberOfSongs = artistSongs.size,
                        numberOfAlbums = artistAlbums.size,
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(horizontal = 5.dp, vertical = 5.dp)
                            .clip(RoundedCornerShape(15.dp))
                    )
                }
                items(
                    items = artistAlbums,
                    key = { it.id }
                ) { album ->
                    AlbumCard(
                        album = album,
                        modifier = Modifier
                            .padding(horizontal = 5.dp, vertical = 5.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .clickable {
                                chargePVMAlbumSongs(album.name)
                                onNavigate(Screen.AlbumsDetails(album.id))
                            },
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                }
            }
            Spacer(modifier = Modifier.width(5.dp))
            LazyColumn {
                itemsIndexed(
                    items = artistSongs,
                    key = { _, music -> music.mediaId }
                ) { index, music ->
                    MusicListItem(
                        music = music,
                        currentMusicUri = currentMusicUri,
                        onShortClick = { onClickPlay(it) },
                        isPlayerReady = isPlayerReady,
                        modifier = Modifier.thenIf(index == 0) { statusBarsPadding() }
                    )
                }
            }
        }
        CuteNavigationButton(
            modifier = Modifier
                .padding(start = 15.dp)
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
        ) { onNavigateUp() }
    }
}

@Composable
private fun SharedTransitionScope.ArtistInfoCard(
    artist: Artist,
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
    numberOfSongs: Int,
    numberOfAlbums: Int
) {
    Column(
        modifier = modifier
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(start = 10.dp)
                .sharedElement(
                    state = rememberSharedContentState(key = artist.id),
                    animatedVisibilityScope = animatedVisibilityScope,
                )
                .size(140.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFFAB3AA)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.artist_rounded),
                contentDescription = stringResource(id = R.string.artwork),
                modifier = Modifier.size(94.dp) // Size of the parent container divided by 1.5
            )
        }
        Spacer(Modifier.height(10.dp))
        Column {
            CuteText(
                text = artist.name,
                maxLines = 1,
                modifier = Modifier
                    .sharedElement(
                        state = rememberSharedContentState(key = artist.name + artist.id),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
            )
            CuteText(pluralStringResource(R.plurals.songs, numberOfSongs, numberOfSongs))
            CuteText(pluralStringResource(R.plurals.albums, numberOfAlbums, numberOfAlbums))
        }
    }
}