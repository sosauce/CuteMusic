package com.sosauce.cutemusic.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.audio.Album
import com.sosauce.cutemusic.audio.getAlbumArt
import com.sosauce.cutemusic.logic.AppBar
import com.sosauce.cutemusic.logic.BottomBar
import com.sosauce.cutemusic.ui.theme.GlobalFont

@Composable
fun AlbumsScreen(
    navController: NavController,
    albums: List<Album>
) {
    AlbumsScreenContent(
        navController = navController,
        albums = albums
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun AlbumsScreenContent(
    navController: NavController,
    albums: List<Album>
) {


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                AppBar(
                    title = "Albums",
                    showBackArrow = false,
                    showMenuIcon = true,
                    navController = navController,
                    showSortIcon = false,
                    viewModel = null,
                    musics = null
                )
            },
            bottomBar = {
                BottomBar(
                    navController = navController
                )
            }
        ) { values ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(values)
            ) {
                items(albums, key = { it.id }) { album ->
                    AlbumCard(album = album) { navController.navigate("AlbumsDetailsScreen") {
                        launchSingleTop = true
                        restoreState = true
                    } }
                }
            }
        }
    }
}

@Composable
private fun AlbumCard(
    album: Album,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var art: ByteArray? by remember { mutableStateOf(byteArrayOf()) }

    LaunchedEffect(album.uri) {
        art = getAlbumArt(context, album)
    }

    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier
            .padding(horizontal = 5.dp, vertical = 5.dp)
            .clickable { onClick() },
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            AsyncImage(
                model = art ?: R.drawable.cute_music_icon,
                contentDescription = "Artwork",
                modifier = Modifier
                    .size(115.dp)
                    .clip(RoundedCornerShape(15)),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (album.name.length >= 25) album.name.take(25) + "..." else album.name,
                    fontFamily = GlobalFont,
                    maxLines = 1
                )
                Text(text = album.artist,
                    fontFamily = GlobalFont
                )
            }
        }
    }
}