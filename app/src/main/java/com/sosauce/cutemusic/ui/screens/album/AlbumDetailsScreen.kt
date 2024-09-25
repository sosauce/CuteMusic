@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.ui.screens.album

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.ui.screens.main.MusicListItem
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
                title = {},
                navigationIcon = {
                    IconButton(onClick = { onPopBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
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
                .padding(
                    start = values.calculateLeftPadding(LayoutDirection.Ltr) + 10.dp,
                    end = values.calculateRightPadding(LayoutDirection.Rtl) + 10.dp,
                    top = values.calculateTopPadding(),
                    bottom = values.calculateBottomPadding()
                )
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AsyncImage(
                        model = ImageUtils.imageRequester(
                            img = ImageUtils.getAlbumArt(album.id),
                            context = context
                        ),
                        contentDescription = stringResource(R.string.artwork),
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        CuteText(
                            text = album.name,
                            fontSize = 22.sp,
                            modifier = Modifier.basicMarquee()
                        )
                        CuteText(
                            text = album.artist,
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                            modifier = Modifier.basicMarquee()
                        )
                        Spacer(Modifier.height(60.dp))
                        CuteText(
                            text = "${albumSongs.size} ${if (albumSongs.size <= 1) "song" else "songs"}",
                            fontSize = 22.sp,
                            modifier = Modifier.basicMarquee()
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Column {
                    albumSongs.forEach { music ->
                        MusicListItem(
                            music = music,
                            onShortClick = { viewModel.itemClicked(it, listOf()) },
                            currentMusicUri = viewModel.currentMusicUri
                        )
                    }
                }
            }
        }
    }
}
