package com.sosauce.cutemusic.ui.screens.album

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.ui.customs.textCutter
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.shared_components.CuteNavigationRail
import com.sosauce.cutemusic.ui.shared_components.NavigationItem
import com.sosauce.cutemusic.ui.shared_components.PostViewModel
import com.sosauce.cutemusic.ui.theme.GlobalFont
import com.sosauce.cutemusic.utils.ImageUtils

@Composable
fun AlbumScreenLandscape(
    navController: NavController,
    albums: List<Album>,
    postViewModel: PostViewModel,
    bottomBarIndex: Int,
    onBottomBarNavigation: (Int, NavigationItem) -> Unit,
) {

    Scaffold { values ->
            if (albums.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(values)
                ) {
                    Text(
                        text = stringResource(id = R.string.no_albums_found),
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontFamily = GlobalFont
                    )

                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(values)
                        .padding(start = 80.dp)
                ) {
                    items(items = albums, key = { it.id }) {album ->
                        AlbumCardLandscape(
                            album = album,
                            onClick = {
                                postViewModel.albumSongs(album.id)
                                navController.navigate(Screen.AlbumsDetails(id = album.id))
                            },
                        )
                    }
                }
            }

        CuteNavigationRail(
            selectedIndex = bottomBarIndex,
            onNavigationItemClicked = onBottomBarNavigation
        )
    }
}

@Composable
private fun AlbumCardLandscape(
    album: Album,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier
            .padding(horizontal = 5.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(15.dp))
            .clickable { onClick() },
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = ImageUtils.imageRequester(
                    img = ImageUtils.getAlbumArt(album.id),
                    context = context
                ),
                contentDescription = "Artwork",
                modifier = Modifier
                    .size(215.dp)
                    .padding(top = 7.dp)
                    .clip(RoundedCornerShape(15)),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = textCutter(album.name, 15),
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
