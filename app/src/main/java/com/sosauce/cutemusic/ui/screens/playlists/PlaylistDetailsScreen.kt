package com.sosauce.cutemusic.ui.screens.playlists

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import com.sosauce.cutemusic.domain.model.Playlist
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.main.MusicListItem
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.utils.thenIf

@Composable
fun PlaylistDetailsScreen(
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
    onPopBackStack: () -> Unit,
) {

    val playlistDisplay = remember {
        if (playlist.emoji.isNotBlank()) {
            "${playlist.emoji} ${playlist.name}"
        } else {
            playlist.name
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn {
            itemsIndexed(
                items = playlist.musics,
                key = { _, id -> id.hashCode() }
            ) { index, id ->
                musics.find { it.mediaId == id }?.let { music ->
                    Column(
                        modifier = Modifier
                            .animateItem()
                            .padding(
                                vertical = 2.dp,
                                horizontal = 4.dp
                            )
                    ) {
                        MusicListItem(
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
                            modifier = Modifier.thenIf(index == 0) {
                                statusBarsPadding()
                            }
                        )
                    }
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(
                    start = 10.dp,
                    bottom = 3.dp
                )
        ) {
            IconButton(
                onClick = onPopBackStack,
                modifier = Modifier.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(50.dp)
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null
                )
            }
            Spacer(Modifier.width(5.dp))
            Box(
                modifier = Modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(50.dp)
                )
            ) {
                CuteText(
                    text = playlistDisplay,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}