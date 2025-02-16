@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.album

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Settings
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.shared_components.CuteActionButton
import com.sosauce.cutemusic.ui.shared_components.CuteSearchbar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.rememberSearchbarAlignment

@Composable
fun SharedTransitionScope.AlbumsScreen(
    albums: List<Album>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    currentlyPlaying: String,
    chargePVMAlbumSongs: (String) -> Unit,
    onNavigate: (Screen) -> Unit,
    currentScreen: String,
    isPlaying: Boolean,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    isPlayerReady: Boolean,
    onNavigationItemClicked: (Screen) -> Unit,
) {
    val isLandscape = rememberIsLandscape()
    var query by remember { mutableStateOf("") }
    var isSortedByASC by remember { mutableStateOf(true) } // I prolly should change this
    val float by animateFloatAsState(
        targetValue = if (isSortedByASC) 45f else 135f,
        label = "Arrow Icon Animation"
    )
    val numberOfGrids = remember {
        if (isLandscape) 4 else 2
    }

    val displayAlbums by remember(isSortedByASC, albums, query) {
        derivedStateOf {
            if (query.isNotEmpty()) {
                albums.filter {
                    it.name.contains(
                        other = query,
                        ignoreCase = true
                    ) == true
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
                    contentPadding = paddingValues
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
                                    chargePVMAlbumSongs(album.name)
                                    onNavigate(Screen.AlbumsDetails(album.id))
                                },
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    }
                }
            }
            CuteSearchbar(
                query = query,
                onQueryChange = { query = it },
                modifier = Modifier.align(rememberSearchbarAlignment()),
                trailingIcon = {
                    Row {
                        IconButton(
                            onClick = { isSortedByASC = !isSortedByASC }
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
                isPlayerReady = isPlayerReady,
                onNavigate = { onNavigate(Screen.NowPlaying) },
                onNavigationItemClicked = onNavigationItemClicked,
                currentScreen = currentScreen,
                fab = {
                    CuteActionButton(
                        modifier = Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "fab"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    ) { onHandlePlayerActions(PlayerActions.PlayRandom) }
                }
            )
        }
    }
}


@Composable
fun SharedTransitionScope.AlbumCard(
    album: Album,
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
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
                .sharedElement(
                    state = rememberSharedContentState(key = album.id),
                    animatedVisibilityScope = animatedVisibilityScope,
                )
                .clip(RoundedCornerShape(24.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(10.dp))
        Column {
            CuteText(
                text = album.name,
                maxLines = 1,
                modifier = Modifier
                    .sharedElement(
                        state = rememberSharedContentState(key = album.name + album.id),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                    .basicMarquee()
            )
            CuteText(
                text = album.artist,
                color = MaterialTheme.colorScheme.onBackground.copy(0.85f),
                modifier = Modifier
                    .sharedElement(
                        state = rememberSharedContentState(key = album.artist + album.id),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                    .basicMarquee()
            )
        }
    }
}

