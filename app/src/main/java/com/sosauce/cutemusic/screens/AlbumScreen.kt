@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.audio.Album
import com.sosauce.cutemusic.logic.AppBar
import com.sosauce.cutemusic.logic.BottomBar
import com.sosauce.cutemusic.logic.imageRequester
import com.sosauce.cutemusic.logic.navigation.Screen
import com.sosauce.cutemusic.ui.theme.GlobalFont

@Composable
fun SharedTransitionScope.AlbumsScreen(
    navController: NavController,
    albums: List<Album>,
    viewModel: MusicViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigate: () -> Unit
) {
    AlbumsScreenContent(
        navController = navController,
        albums = albums,
        viewModel = viewModel,
        animatedVisibilityScope = animatedVisibilityScope,
        onNavigate = { onNavigate() }

    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun SharedTransitionScope.AlbumsScreenContent(
    navController: NavController,
    albums: List<Album>,
    viewModel: MusicViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigate: () -> Unit
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
                    onNavigate = { onNavigate() }
                )

            },
            bottomBar = {
                BottomBar(navController = navController, viewModel = viewModel)
            }
        ) { values ->

            if (albums.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(values)
                ) {
                    Text(
                        text = "No album found !",
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
                    itemsIndexed(albums) { index, album ->
                        AlbumCard(
                            album = album,
                            onClick = {
                                //navController.navigate("AlbumsDetailsScreen/$index")
                                navController.navigate(Screen.AlbumsDetails(id = index))
                            },
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SharedTransitionScope.AlbumCard(
    album: Album,
    onClick: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
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
        ) {
            AsyncImage(
                model = imageRequester(
                    img = album.albumArt,
                    context = context
                ),
                contentDescription = "Artwork",
                modifier = Modifier
                    .aspectRatio(1 / 1f)
                    .padding(7.dp)
                    .clip(RoundedCornerShape(15))
                    .sharedElement(
                        state = rememberSharedContentState(key = album.id),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ ->
                            tween(durationMillis = 1000)
                        }
                    ),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (album.name.length >= 15) album.name.take(15) + "..." else album.name,
                    fontFamily = GlobalFont,
                    maxLines = 1
                )
                Text(
                    text = album.artist,
                    fontFamily = GlobalFont
                )
            }
        }
    }
}
