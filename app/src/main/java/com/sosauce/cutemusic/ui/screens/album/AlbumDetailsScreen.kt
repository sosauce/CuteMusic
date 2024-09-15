@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.ui.screens.album

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.shared_components.PostViewModel
import com.sosauce.cutemusic.utils.ImageUtils

@Composable
fun AlbumDetailsScreen(
    album: Album,
    viewModel: MusicViewModel,
    postViewModel: PostViewModel,
    onPopBackStack: () -> Unit,
) {
    val albumSongs by remember { mutableStateOf(postViewModel.albumSongs) }

    if (rememberIsLandscape()) {
        AlbumDetailsLandscape(
            album = album,
            onNavigateUp = { onPopBackStack() },
            postViewModel = postViewModel,
            viewModel = viewModel,
        )
    } else {
        AlbumDetailsContent(
            album = album,
            viewModel = viewModel,
            onPopBackStack = { onPopBackStack() },
            albumSongs = albumSongs,
        )
    }

}

@Composable
private fun AlbumDetailsContent(
    album: Album,
    viewModel: MusicViewModel,
    onPopBackStack: () -> Unit,
    albumSongs: List<MediaItem>,

    ) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    CuteText(
                        text = album.name,

                        )
                },
                navigationIcon = {
                    IconButton(onClick = { onPopBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back arrow"
                        )
                    }
                }
            )
        }
    ) { values ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(values)
        ) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AsyncImage(
                        model = ImageUtils.imageRequester(
                            img = ImageUtils.getAlbumArt(album.id),
                            context = context
                        ),
                        contentDescription = "Album Art",
                        modifier = Modifier
                            .aspectRatio(1 / 1f)
                            .padding(17.dp)
                            .clip(RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CuteText(
                            text = if (album.artist.length >= 18) album.artist.take(18) + "..." + " · " else album.artist + " · ",

                            fontSize = 22.sp
                        )
                        CuteText(
                            text = "${albumSongs.size} ${if (albumSongs.size <= 1) "song" else "songs"}",

                            fontSize = 22.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                HorizontalDivider()
                LazyColumn {
                    itemsIndexed(albumSongs) { _, music ->
                        AlbumSong(
                            music = music,
                            onShortClick = { viewModel.itemClicked(it, listOf()) },

                            )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumSong(
    music: MediaItem,
    onShortClick: (String) -> Unit,
) {

    val context = LocalContext.current


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onShortClick(music.mediaId) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = ImageUtils.imageRequester(
                    img = music.mediaMetadata.artworkUri,
                    context = context
                ),
                stringResource(R.string.artwork),
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(45.dp),
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                CuteText(
                    text = music.mediaMetadata.title.toString(),
                    maxLines = 1,
                    modifier = Modifier.then(
                        if (music.mediaMetadata.title?.length!! >= 25) {
                            Modifier.basicMarquee()
                        } else Modifier
                    )
                )
                CuteText(
                    text = music.mediaMetadata.artist.toString(),
                    color = MaterialTheme.colorScheme.onBackground.copy(0.85f)
                )
            }
        }

    }
}
