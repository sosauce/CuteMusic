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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Settings
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
import com.sosauce.cutemusic.data.datastore.rememberSortASCAlbums
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.shared_components.CuteSearchbar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.shared_components.NavigationItem
import com.sosauce.cutemusic.ui.shared_components.PostViewModel
import com.sosauce.cutemusic.ui.shared_components.ScreenSelection
import com.sosauce.cutemusic.ui.shared_components.SortRadioButtons
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.thenIf

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
            isPlaylistEmpty = viewModel.isPlaylistEmptyAndDataNotNull()
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
    var sort by rememberSortASCAlbums()
    var sortExpanded by remember { mutableStateOf(false) }
    var screenSelectionExpanded by remember { mutableStateOf(false) }
    val displayAlbums by remember {
        derivedStateOf {
            if (query.isNotEmpty()) {
                albums.filter {
                    it.name.contains(
                        other = query,
                        ignoreCase = true
                    )
                }
            } else {
                if (sort) albums
                else albums.sortedByDescending { it.name }
            }
        }
    }


    Box {

            if (albums.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
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
                ) {
                    itemsIndexed(
                        items = displayAlbums,
                        key = { _, album -> album.id }
                    ) { index, album ->
                        AlbumCard(
                            album = album,
                            modifier = Modifier
                                .clip(RoundedCornerShape(15.dp))
                                .clickable {
                                    chargePVMAlbumSongs(album.id)
                                    onNavigate(Screen.AlbumsDetails(album.id))
                                }
                                .thenIf(
                                    index == 0 || index == 1,
                                    Modifier.statusBarsPadding()
                                )
                        )
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
                Row {
                    IconButton(onClick = { sortExpanded = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Sort,
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = { onNavigate(Screen.Settings) }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = null
                        )
                    }
                    DropdownMenu(
                        expanded = sortExpanded,
                        onDismissRequest = { sortExpanded = false },
                        modifier = Modifier
                            .width(180.dp)
                            .background(color = MaterialTheme.colorScheme.surface)
                    ) {
                        SortRadioButtons(
                            sort = sort,
                            onChangeSort = { sort = !sort }
                        )
                    }
                }
            },
            currentlyPlaying = currentlyPlaying,
            onHandlePlayerActions = { onHandlePlayerActions(it) },
            isPlaying = isPlaying,
            animatedVisibilityScope = animatedVisibilityScope,
            isPlaylistEmpty = isPlaylistEmpty,
            onNavigate = { onNavigate(Screen.NowPlaying) }
        )
    }
}

@Composable
fun AlbumCard(
    album: Album,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .padding(20.dp)
    ) {
        AsyncImage(
            model = ImageUtils.imageRequester(
                img = ImageUtils.getAlbumArt(album.id) ?: R.drawable.ic_launcher_foreground,
                context = context
            ),
            contentDescription = stringResource(id = R.string.artwork),
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(24.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(10.dp))
        Column {
            CuteText(
                text = album.name,
                maxLines = 1,
                modifier = Modifier.basicMarquee()
            )
            CuteText(
                text = album.artist,
                color = MaterialTheme.colorScheme.onBackground.copy(0.85f),
                modifier = Modifier.basicMarquee()
            )
        }
    }
}

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