@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.album

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberAlbumGrids
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.main.components.SortingDropdownMenu
import com.sosauce.cutemusic.ui.shared_components.CuteActionButton
import com.sosauce.cutemusic.ui.shared_components.CuteDropdownMenuItem
import com.sosauce.cutemusic.ui.shared_components.CuteSearchbar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.SharedTransitionKeys
import com.sosauce.cutemusic.utils.rememberSearchbarAlignment
import com.sosauce.cutemusic.utils.showCuteSearchbar

@Composable
fun SharedTransitionScope.AlbumsScreen(
    albums: List<Album>,
    currentlyPlaying: String,
    onNavigate: (Screen) -> Unit,
    isPlaying: Boolean,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    isPlayerReady: Boolean,
) {
    var numberOfAlbumGrids by rememberAlbumGrids()
    val isLandscape = rememberIsLandscape()
    var query by remember { mutableStateOf("") }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var gridSelectionExpanded by remember { mutableStateOf(false) }
    var isSortedByASC by rememberSaveable { mutableStateOf(true) } // I prolly should change this
    val numberOfGrids = remember(numberOfAlbumGrids) {
        if (isLandscape) 4 else numberOfAlbumGrids
    }
    val state = rememberLazyGridState()
    val displayAlbums by remember(isSortedByASC, albums, query) {
        derivedStateOf {
            if (query.isNotEmpty()) {
                albums.filter {
                    it.name.contains(
                        other = query,
                        ignoreCase = true
                    )
                }
            } else {
                if (isSortedByASC) albums
                else albums.sortedByDescending { it.name }
            }

        }
    }
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Box {
            if (displayAlbums.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
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
                        .fillMaxSize(),
                    contentPadding = paddingValues,
                    state = state
                ) {
                    items(
                        items = displayAlbums,
                        key = { it.id }
                    ) { album ->
                        AlbumCard(
                            album = album,
                            modifier = Modifier
                                .animateItem()
                                .clip(RoundedCornerShape(15.dp))
                                .clickable {
                                    onNavigate(Screen.AlbumsDetails(album.id))
                                }
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = state.showCuteSearchbar,
                modifier = Modifier.align(rememberSearchbarAlignment()),
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                CuteSearchbar(
                    query = query,
                    onQueryChange = { query = it },
                    isScrolling = state.isScrollInProgress,
                    trailingIcon = {
                        val numberOfGrids = setOf(2, 3, 4)

                        IconButton(
                            onClick = { sortMenuExpanded = true }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Sort,
                                contentDescription = stringResource(R.string.sort)
                            )
                        }
                        SortingDropdownMenu(
                            expanded = sortMenuExpanded,
                            onDismissRequest = { sortMenuExpanded = false },
                            isSortedByASC = isSortedByASC,
                            onChangeSorting = { isSortedByASC = it },
                            additionalActions = {
                                CuteDropdownMenuItem(
                                    onClick = { gridSelectionExpanded = true },
                                    text = { CuteText(stringResource(R.string.no_of_grids)) },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.grid_view),
                                            contentDescription = null
                                        )
                                    },
                                    trailingIcon = { CuteText(numberOfAlbumGrids.toString()) }
                                )
                            }
                        )
                        DropdownMenu(
                            expanded = gridSelectionExpanded,
                            onDismissRequest = { gridSelectionExpanded = false },
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            numberOfGrids.forEach { number ->
                                CuteDropdownMenuItem(
                                    onClick = {
                                        numberOfAlbumGrids = number
                                        gridSelectionExpanded = false
                                    },
                                    text = { CuteText(number.toString()) },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.grid_view),
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                        }
                    },
                    currentlyPlaying = currentlyPlaying,
                    onHandlePlayerActions = onHandlePlayerActions,
                    isPlaying = isPlaying,
                    isPlayerReady = isPlayerReady,
                    onNavigate = onNavigate,
                    fab = {
                        CuteActionButton(
                            modifier = Modifier.sharedBounds(
                                sharedContentState = rememberSharedContentState(key = SharedTransitionKeys.FAB),
                                animatedVisibilityScope = LocalNavAnimatedContentScope.current
                            )
                        ) { onHandlePlayerActions(PlayerActions.PlayRandom) }
                    }
                )
            }
        }
    }
}


@Composable
fun SharedTransitionScope.AlbumCard(
    album: Album,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(15.dp)
    ) {
        AsyncImage(
            model = ImageUtils.imageRequester(
                ImageUtils.getAlbumArt(album.id) ?: androidx.media3.session.R.drawable.media3_icon_album
            ),
            contentDescription = stringResource(id = R.string.artwork),
            modifier = Modifier
                .sharedElement(
                    sharedContentState = rememberSharedContentState(key = album.id),
                    animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                )
                .sizeIn(maxHeight = 160.dp)
                .clip(RoundedCornerShape(24.dp)),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.height(10.dp))
        Column {
            CuteText(
                text = album.name,
                maxLines = 1,
                modifier = Modifier
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = album.name + album.id),
                        animatedVisibilityScope = LocalNavAnimatedContentScope.current
                    )
                    .basicMarquee()
            )
            CuteText(
                text = album.artist,
                color = MaterialTheme.colorScheme.onBackground.copy(0.85f),
                modifier = Modifier
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = album.artist + album.id),
                        animatedVisibilityScope = LocalNavAnimatedContentScope.current
                    )
                    .basicMarquee()
            )
        }
    }
}

