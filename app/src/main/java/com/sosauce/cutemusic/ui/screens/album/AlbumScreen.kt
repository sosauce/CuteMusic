package com.sosauce.cutemusic.ui.screens.album

import android.annotation.SuppressLint
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
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.ui.customs.textCutter
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.shared_components.AppBar
import com.sosauce.cutemusic.ui.shared_components.BottomBar
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.shared_components.NavigationItem
import com.sosauce.cutemusic.ui.shared_components.PostViewModel
import com.sosauce.cutemusic.ui.theme.GlobalFont
import com.sosauce.cutemusic.utils.ImageUtils

@Composable
fun AlbumsScreen(
    navController: NavController,
    albums: List<Album>,
    viewModel: MusicViewModel,
    postViewModel: PostViewModel,
) {
    val isLandscape = rememberIsLandscape()

    if (isLandscape) {
        AlbumScreenLandscape(
            navController = navController,
            albums = albums,
            postViewModel = postViewModel,
            bottomBarIndex = viewModel.selectedItem,
            onBottomBarNavigation = { index, item ->
                navController.navigate(item.navigateTo) {
                    viewModel.selectedItem = index
                    launchSingleTop = true
                }
            },
        )
    } else {
        AlbumsScreenContent(
            albums = albums,
            onNavigate = { navController.navigate(it) },
            onBottomBarNavigation = { index, item ->
                navController.navigate(item.navigateTo) {
                    viewModel.selectedItem = index
                    launchSingleTop = true
                }
            },
            bottomBarIndex = viewModel.selectedItem,
            chargePVMAlbumSongs = { postViewModel.albumSongs(it) }
        )
    }

}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun AlbumsScreenContent(
    albums: List<Album>,
    onNavigate: (Screen) -> Unit,
    bottomBarIndex: Int,
    onBottomBarNavigation: (Int, NavigationItem) -> Unit,
    chargePVMAlbumSongs: (Long) -> Unit

) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                AppBar(
                    title = stringResource(id = R.string.albums),
                    showBackArrow = false,
                    showMenuIcon = true,
                    onNavigate = { onNavigate(Screen.Settings) }
                )

            },
            bottomBar = {
                BottomBar(
                    selectedIndex = bottomBarIndex,
                    onNavigationItemClicked = onBottomBarNavigation
                )
            }
        ) { values ->

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
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(values)
                ) {
                    items(items = albums, key = { it.id }) {album ->
                        AlbumCard(
                            album = album,
                            modifier = Modifier
                                .padding(horizontal = 5.dp, vertical = 5.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .clickable {
                                    chargePVMAlbumSongs(album.id)
                                    onNavigate(Screen.AlbumsDetails(album.id))
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AlbumCard(
    album: Album,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        modifier = modifier
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
                contentDescription = stringResource(id = R.string.artwork),
                modifier = Modifier
                    .aspectRatio(1 / 1f)
                    .padding(10.dp)
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
                    fontFamily = GlobalFont,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.85f)
                )
            }
        }
    }
}

// Previews are commented by default, un-comment to use them, re-comment them when finalizing your changes for a PR

//@Preview
//@Composable
//private fun AlbumScreenPreview() {
//    CuteMusicTheme {
//        AlbumsScreenContent(
//            albums = emptyList(),
//            onNavigate = {},
//            bottomBarIndex = 1,
//            onBottomBarNavigation = {_, _ ->}
//        ) {
//
//        }
//    }
//}