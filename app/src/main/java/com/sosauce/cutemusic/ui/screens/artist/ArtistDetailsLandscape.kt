package com.sosauce.cutemusic.ui.screens.artist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.domain.model.Artist
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.album.AlbumCard
import com.sosauce.cutemusic.ui.screens.main.MusicListItem
import com.sosauce.cutemusic.ui.shared_components.CuteText

@Composable
fun ArtistDetailsLandscape(
    onNavigateUp: () -> Unit,
    artistAlbums: List<Album>,
    artistSongs: List<MediaItem>,
    onClickPlay: (String) -> Unit,
    onNavigate: (Screen) -> Unit,
    chargePVMAlbumSongs: (Long) -> Unit,
    artist: Artist,
    currentMusicUri: String
) {
    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .statusBarsPadding()
            .padding(start = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onNavigateUp() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
            }
            CuteText(
                text = artist.name + " · ",
                fontSize = 20.sp
            )
            CuteText(
                text = "${artistSongs.size} ${if (artistSongs.size <= 1) "song" else "songs"}",
                fontSize = 20.sp
            )
        }
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn {
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
                                chargePVMAlbumSongs(album.id)
                                onNavigate(Screen.AlbumsDetails(album.id))
                            }
                            .size(230.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(5.dp))
            LazyColumn {
                items(artistSongs, key = { it.mediaId }) { music ->
                    MusicListItem(
                        music = music,
                        currentMusicUri = currentMusicUri,
                        onShortClick = { onClickPlay(it) }
                    )
                }
            }
        }
    }
}