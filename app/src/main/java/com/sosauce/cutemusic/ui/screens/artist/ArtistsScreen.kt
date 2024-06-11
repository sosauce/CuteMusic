package com.sosauce.cutemusic.ui.screens.artist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.domain.model.Artist
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
fun ArtistsScreen(
    artist: List<Artist>,
    navController: NavController,
    viewModel: MusicViewModel,
    postViewModel: PostViewModel
) {

    val isLandscape = rememberIsLandscape()

    if (isLandscape) {
        ArtistsScreenLandscape(
            navController = navController,
            artists = artist,
            postViewModel = postViewModel,
            bottomBarIndex = viewModel.selectedItem,
            onBottomBarNavigation = { index, item ->
                navController.navigate(item.navigateTo) {
                    viewModel.selectedItem = index
                    launchSingleTop = true
                }
            }
        )
    } else {
        ArtistsScreenContent(
            artist = artist,
            onNavigate = { navController.navigate(it) },
            onBottomBarNavigation = { index, item ->
                navController.navigate(item.navigateTo) {
                    viewModel.selectedItem = index
                    launchSingleTop = true
                }
            },
            bottomBarIndex = viewModel.selectedItem,
            chargePVMLists = {
                postViewModel.artistSongs(it)
                postViewModel.artistAlbums(it)
            }
        )
    }

}


@Composable
private fun ArtistsScreenContent(
    artist: List<Artist>,
    onNavigate: (Screen) -> Unit,
    bottomBarIndex: Int,
    onBottomBarNavigation: (Int, NavigationItem) -> Unit,
    chargePVMLists: (name: String) -> Unit
) {
        Scaffold(
            topBar = {
                AppBar(
                    title = stringResource(id = R.string.artists),
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

            if (artist.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(values)
                ) {
                    Text(
                        text = stringResource(id = R.string.no_artists_found),
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontFamily = GlobalFont
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(values),
                ) {
                    items(
                        items = artist,
                        key = { it.id }
                    ) {
                        ArtistInfoList(it) {
                            chargePVMLists(it.name)
                            onNavigate(Screen.ArtistsDetails(it.id))
                        }
                    }
                }
            }
        }
}


@Composable
private fun ArtistInfoList(
    artist: Artist,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = ImageUtils.imageRequester(
                    img = R.drawable.artist,
                    context = context
                ),
                contentDescription = stringResource(id = R.string.artwork),
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(45.dp),
                contentScale = ContentScale.Crop,
            )

            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = textCutter(artist.name, 25),
                    fontFamily = GlobalFont,
                    maxLines = 1
                )

            }
        }
    }
}
