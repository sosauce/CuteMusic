package com.sosauce.cutemusic.screens

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
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.audio.Artist
import com.sosauce.cutemusic.logic.AppBar
import com.sosauce.cutemusic.logic.BottomBar
import com.sosauce.cutemusic.logic.imageRequester
import com.sosauce.cutemusic.logic.navigation.Screen
import com.sosauce.cutemusic.ui.theme.GlobalFont

@Composable
fun ArtistsScreen(
    artist: List<Artist>,
    navController: NavController,
    viewModel: MusicViewModel,
    onNavigate: () -> Unit
) {
    ArtistsScreenContent(
        artist = artist,
        navController = navController,
        viewModel = viewModel,
        onNavigate = { onNavigate() }
    )
}

@Composable
private fun ArtistsScreenContent(
    artist: List<Artist>,
    navController: NavController,
    viewModel: MusicViewModel,
    onNavigate: () -> Unit
) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                AppBar(
                    title = "Artists",
                    showBackArrow = false,
                    showMenuIcon = true,
                    onNavigate = { onNavigate() }
                )

            },
            bottomBar = {
                BottomBar(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        ) { values ->

            if (artist.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(values)
                ) {
                    Text(
                        text = "No artist found !",
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontFamily = GlobalFont
                    )

                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(values)
                ) {
                    itemsIndexed(artist) { index, album ->
                        AlbumCard(album) {
                            navController.navigate(Screen.ArtistsDetails(index))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlbumCard(
    artist: Artist,
    onClick: () -> Unit
) {
    val context = LocalContext.current
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
                model = imageRequester(
                    img = R.drawable.artist,
                    context = context
                ),
                contentDescription = "Artwork",
                modifier = Modifier
                    .size(145.dp)
                    .clip(RoundedCornerShape(15)),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (artist.name.length >= 25) artist.name.take(25) + "..." else artist.name,
                    fontFamily = GlobalFont,
                    maxLines = 1
                )
            }
        }
    }
}