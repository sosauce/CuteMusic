@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.album

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.shared_components.CuteSearchbar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.shared_components.NavigationItem
import com.sosauce.cutemusic.ui.shared_components.PostViewModel
import com.sosauce.cutemusic.ui.shared_components.ScreenSelection
import com.sosauce.cutemusic.utils.ImageUtils

@Composable
fun SharedTransitionScope.AlbumsScreen(
    navController: NavController,
    albums: List<Album>,
    viewModel: MusicViewModel,
    postViewModel: PostViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val isLandscape = rememberIsLandscape()

    if (isLandscape) {
        AlbumScreenLandscape(
            albums = albums,
            selectedIndex = viewModel.selectedItem,
            onNavigateTo = { navController.navigate(it) },
            currentlyPlaying = viewModel.currentlyPlaying,
            isCurrentlyPlaying = viewModel.isCurrentlyPlaying,
            onNavigationItemClicked = { index, item ->
                navController.navigate(item.navigateTo) {
                    viewModel.selectedItem = index
                    launchSingleTop = true
                }
            },
            viewModel = viewModel,
            animatedVisibilityScope = animatedVisibilityScope,
            navController = navController,
            postViewModel = postViewModel

        )
    } else {
        AlbumsScreenContent(
            albums = albums,
            onNavigate = { navController.navigate(it) },
            onNavigationItemClicked = { index, item ->
                navController.navigate(item.navigateTo) {
                    viewModel.selectedItem = index
                    launchSingleTop = true
                }
            },
            selectedIndex = viewModel.selectedItem,
            chargePVMAlbumSongs = postViewModel::albumSongs,
            currentlyPlaying = viewModel.currentlyPlaying,
            onHandlePlayerActions = viewModel::handlePlayerActions,
            isPlaying = viewModel.isCurrentlyPlaying,
            animatedVisibilityScope = animatedVisibilityScope,
            isPlaylistEmpty = viewModel.isPlaylistEmpty()
        )
    }

}

@Composable
private fun SharedTransitionScope.AlbumsScreenContent(
    albums: List<Album>,
    onNavigate: (Screen) -> Unit,
    onNavigationItemClicked: (Int, NavigationItem) -> Unit,
    selectedIndex: Int,
    chargePVMAlbumSongs: (Long) -> Unit,
    currentlyPlaying: String,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    isPlaying: Boolean,
    animatedVisibilityScope: AnimatedVisibilityScope,
    isPlaylistEmpty: Boolean
) {

    var query by remember { mutableStateOf("") }
    var screenSelectionExpanded by remember { mutableStateOf(false) }
    val displayAlbums by remember(query) {
        derivedStateOf {
            if (query.isNotEmpty()) {
                albums.filter {
                    it.name.contains(
                        other = query,
                        ignoreCase = true
                    )
                }
            } else albums
        }
    }


    Box {
        Scaffold { values ->

            if (albums.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(values)
                ) {
                    CuteText(
                        text = stringResource(id = R.string.no_albums_found),
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,

                        )

                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(values)
                ) {
                    items(
                        items = displayAlbums,
                        key = { it.id }
                    ) { album ->
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
        CuteSearchbar(
            query = query,
            onQueryChange = { query = it },
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth(0.9f)
                .padding(bottom = 10.dp)
                .align(Alignment.BottomCenter)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(24.dp)
                )
                .clip(RoundedCornerShape(24.dp))
                .clickable { onNavigate(Screen.NowPlaying) }
                .sharedElement(
                    state = rememberSharedContentState(key = "searchbar"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = { _, _ ->
                        tween(durationMillis = 500)
                    }
                ),
            placeholder = {
                CuteText(
                    text = stringResource(id = R.string.search) + " " + stringResource(R.string.albums),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),

                    )
            },
            leadingIcon = {
                IconButton(onClick = { screenSelectionExpanded = true }) {
                    Icon(
                        imageVector = Icons.Rounded.Album,
                        contentDescription = null
                    )
                }

                DropdownMenu(
                    expanded = screenSelectionExpanded,
                    onDismissRequest = { screenSelectionExpanded = false },
                    modifier = Modifier
                        .width(180.dp)
                        .background(color = MaterialTheme.colorScheme.surface)
                ) {
                    ScreenSelection(
                        onNavigationItemClicked = onNavigationItemClicked,
                        selectedIndex = selectedIndex
                    )
                }
            },
            trailingIcon = {
                IconButton(onClick = { onNavigate(Screen.Settings) }) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = null
                    )
                }
            },
            currentlyPlaying = currentlyPlaying,
            onHandlePlayerActions = { onHandlePlayerActions(it) },
            isPlaying = isPlaying,
            animatedVisibilityScope = animatedVisibilityScope,
            isPlaylistEmpty = isPlaylistEmpty
        )
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
                CuteText(
                    text = album.name,
                    maxLines = 1,
                    modifier = Modifier.then(
                        if (album.name.length >= 15) {
                            Modifier.basicMarquee()
                        } else Modifier
                    )
                )
                CuteText(
                    text = album.artist,

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