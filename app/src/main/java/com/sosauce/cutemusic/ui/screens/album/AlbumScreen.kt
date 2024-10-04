@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.album

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
import com.sosauce.cutemusic.utils.SortingType
import com.sosauce.cutemusic.utils.rememberSearchbarAlignment
import com.sosauce.cutemusic.utils.rememberSearchbarMaxFloatValue
import com.sosauce.cutemusic.utils.rememberSearchbarRightPadding
import com.sosauce.cutemusic.utils.thenIf

@Composable
fun SharedTransitionScope.AlbumsScreen(
    albums: List<Album>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onHandleSorting: (SortingType) -> Unit,
    onHandleSearching: (String) -> Unit,
    currentlyPlaying: String,
    chargePVMAlbumSongs: (String) -> Unit,
    onNavigate: (Screen) -> Unit,
    selectedIndex: Int,
    isPlaying: Boolean,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    isPlaylistEmpty: Boolean,
    onNavigationItemClicked: (Int, NavigationItem) -> Unit
) {
    val isLandscape = rememberIsLandscape()
    var query by remember { mutableStateOf("") }
    var screenSelectionExpanded by remember { mutableStateOf(false) }
    var isSortedByASC by remember { mutableStateOf(true) } // I prolly should change this
    val float by animateFloatAsState(
        targetValue = if (isSortedByASC) 45f else 135f,
        label = "Arrow Icon Animation"
    )
    val numberOfGrids = remember {
        if (isLandscape) 4 else 2
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
                    textAlign = TextAlign.Center
                )

            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(numberOfGrids),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                itemsIndexed(
                    items = albums,
                    key = { _, album -> album.id }
                ) { index, album ->
                    AlbumCard(
                        album = album,
                        modifier = Modifier
                            .animateItem()
                            .clip(RoundedCornerShape(15.dp))
                            .clickable {
                                chargePVMAlbumSongs(album.name)
                                onNavigate(Screen.AlbumsDetails(album.id))
                            }
//                            .thenIf(
//                                index == 0 || index == 1 || index == 2 || index == 3, // booo bad
//                                Modifier.statusBarsPadding()
//                            )
                    )
                }
            }
        }
        CuteSearchbar(
            query = query,
            onQueryChange = {
                query = it
                onHandleSearching(query)
            },
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth(rememberSearchbarMaxFloatValue())
                .padding(
                    bottom = 5.dp,
                    end = rememberSearchbarRightPadding()
                )
                .align(rememberSearchbarAlignment())
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
                    IconButton(
                        onClick = {
                            isSortedByASC = !isSortedByASC
                            when (isSortedByASC) {
                                true -> {
                                    onHandleSorting(SortingType.ASCENDING)
                                }

                                false -> {
                                    onHandleSorting(SortingType.DESCENDING)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowUpward,
                            contentDescription = null,
                            modifier = Modifier.rotate(float)
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
                }
            },
            currentlyPlaying = currentlyPlaying,
            onHandlePlayerActions = onHandlePlayerActions,
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