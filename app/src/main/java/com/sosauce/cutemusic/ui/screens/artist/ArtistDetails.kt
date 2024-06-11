@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.sosauce.cutemusic.ui.screens.artist

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.domain.model.Artist
import com.sosauce.cutemusic.domain.model.Music
import com.sosauce.cutemusic.ui.customs.textCutter
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.main.components.BottomSheetContent
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.shared_components.PostViewModel
import com.sosauce.cutemusic.ui.theme.GlobalFont
import com.sosauce.cutemusic.utils.ImageUtils

@Composable
fun ArtistDetails(
    artist: Artist,
    navController: NavController,
    viewModel: MusicViewModel,
    postViewModel: PostViewModel
) {

    val artistSongs by remember { mutableStateOf(postViewModel.artistSongs) }
    val artistAlbums by remember { mutableStateOf(postViewModel.artistAlbums) }

    val isLandscape = rememberIsLandscape()

    if (isLandscape) {
        ArtistDetailsLandscape(
            onNavigateUp = { navController.popBackStack() },
            artistAlbums = artistAlbums,
            artistSongs = artistSongs,
            onClickPlay = { viewModel.playAtIndex(it) },
            onNavigate = { navController.navigate(it) },
            chargePVMAlbumSongs = { postViewModel.albumSongs(it) },
            artist = artist
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = artist.name + " · ",
                                fontFamily = GlobalFont,
                                fontSize = 20.sp
                            )
                            Text(
                                text = "${artistSongs.size} ${if (artistSongs.size <= 1) "song" else "songs"}",
                                fontFamily = GlobalFont,
                                fontSize = 20.sp
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
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
                    LazyRow {
                        items(items = artistAlbums, key = { it.id }) { album ->
                            AlbumsCard(album) {
                                navController.navigate(Screen.AlbumsDetails(album.id))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn {
                        items(artistSongs) {music ->
                            ArtistMusicList(
                                music = music,
                                onShortClick = { viewModel.playAtIndex(music.uri) },
                                onSelected = { /*TODO*/ },
                                isSelected = false
                            )
                        }
                    }

                }
            }
        }
    }

}

@Composable
private fun AlbumsCard(
    album: Album,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .clickable { onClick() },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            AsyncImage(
                model = ImageUtils.imageRequester(
                    img = ImageUtils.getAlbumArt(album.id),
                    context = context
                ),
                contentDescription = "Album Art",
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(15)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = textCutter(album.name, 15),
                fontFamily = GlobalFont
            )
        }
    }
}

@Composable
fun ArtistMusicList(
    music: Music,
    onShortClick: (Uri) -> Unit,
    onSelected: () -> Unit,
    isSelected: Boolean
) {

    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current
    var isSheetOpen by remember { mutableStateOf(false) }
    var art: Bitmap? by remember { mutableStateOf(null) }

    LaunchedEffect(music.uri) {
        art = ImageUtils.getMusicArt(context, music.uri)
    }

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
            .combinedClickable(
                onClick = { onShortClick(music.uri) },
                onLongClick = { onSelected() }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!isSelected) {
                AsyncImage(
                    model = ImageUtils.imageRequester(
                        img = art,
                        context = context
                    ),
                    contentDescription = "Artwork",
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(45.dp)
                        .clip(RoundedCornerShape(15)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = rememberAsyncImagePainter(art ?: R.drawable.cute_music_icon),
                    contentDescription = "Artwork",
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(45.dp)
                        .clip(RoundedCornerShape(15)),
                    contentScale = ContentScale.Crop,
                )
            }

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