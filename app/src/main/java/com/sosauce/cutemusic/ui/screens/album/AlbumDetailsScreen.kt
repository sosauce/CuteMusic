@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.album

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.domain.model.Music
import com.sosauce.cutemusic.ui.customs.textCutter
import com.sosauce.cutemusic.ui.screens.main.components.BottomSheetContent
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.shared_components.PostViewModel
import com.sosauce.cutemusic.ui.theme.GlobalFont
import com.sosauce.cutemusic.utils.ImageUtils

@Composable
fun AlbumDetailsScreen(
    album: Album,
    viewModel: MusicViewModel,
    postViewModel: PostViewModel,
    onPopBackStack: () -> Unit
) {
    val albumSongs by remember { mutableStateOf(postViewModel.albumSongs) }

    val config = LocalConfiguration.current

    if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
        AlbumDetailsContent(
            album = album,
            viewModel = viewModel,
            onPopBackStack = { onPopBackStack() },
            albumSongs = albumSongs
        )
    } else {
        AlbumDetailsLandscape(
            album = album,
            onNavigateUp = { onPopBackStack() },
            postViewModel = postViewModel,
            viewModel = viewModel
        )
    }

}

@Composable
private fun AlbumDetailsContent(
    album: Album,
    viewModel: MusicViewModel,
    onPopBackStack: () -> Unit,
    albumSongs: List<Music>
) {
        val context = LocalContext.current
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = album.name,
                            fontFamily = GlobalFont
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
                            Text(
                                text = if (album.artist.length >= 18) album.artist.take(18) + "..." + " · " else album.artist + " · ",
                                fontFamily = GlobalFont,
                                fontSize = 22.sp
                            )
                            Text(
                                text = "${albumSongs.size} ${if (albumSongs.size <= 1) "song" else "songs"}",
                                fontFamily = GlobalFont,
                                fontSize = 22.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    HorizontalDivider()
                    LazyColumn {
                        itemsIndexed(albumSongs) { _, music ->
                            AlbumSong(music = music, onShortClick = { viewModel.playAtIndex(music.uri) })
                        }
                    }
                }
            }
        }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumSong(
    music: Music,
    onShortClick: (Uri) -> Unit
) {

    val sheetState = rememberModalBottomSheetState(false)
    var isSheetOpen by remember { mutableStateOf(false) }

    if (isSheetOpen) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = sheetState,
            onDismissRequest = { isSheetOpen = false }
        ) {
            BottomSheetContent(music)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onShortClick(music.uri) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                Text(
                    text = textCutter(music.name, 25),
                    fontFamily = GlobalFont,
                    maxLines = 1
                )
                Text(
                    text = music.artist,
                    fontFamily = GlobalFont
                )
            }
        }
        IconButton(onClick = { isSheetOpen = true }) {
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = null
            )
        }
    }
}
