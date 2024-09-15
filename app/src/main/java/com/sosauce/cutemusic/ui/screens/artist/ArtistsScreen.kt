@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.artist

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
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
import com.sosauce.cutemusic.domain.model.Artist
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.shared_components.CuteSearchbar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.shared_components.NavigationItem
import com.sosauce.cutemusic.ui.shared_components.PostViewModel
import com.sosauce.cutemusic.ui.shared_components.ScreenSelection
import com.sosauce.cutemusic.utils.ImageUtils

@Composable
fun SharedTransitionScope.ArtistsScreen(
    artist: List<Artist>,
    navController: NavController,
    viewModel: MusicViewModel,
    postViewModel: PostViewModel,
    animatedVisibilityScope: AnimatedVisibilityScope
) {

    val isLandscape = rememberIsLandscape()

    if (isLandscape) {
        ArtistsScreenLandscape(
            artists = artist,
            chargePVMLists = {
                postViewModel.artistSongs(it)
                postViewModel.artistAlbums(it)
            },
            onNavigateTo = { navController.navigate(it) },
            currentlyPlaying = viewModel.currentlyPlaying,
            isCurrentlyPlaying = viewModel.isCurrentlyPlaying,
            animatedVisibilityScope = animatedVisibilityScope,
            selectedIndex = viewModel.selectedItem,
            onNavigationItemClicked = { index, item ->
                navController.navigate(item.navigateTo) {
                    viewModel.selectedItem = index
                    launchSingleTop = true
                }
            },
            onHandlePlayerActions = { viewModel.handlePlayerActions(it) },
            isPlaylistEmpty = viewModel.isPlaylistEmpty()

        )
    } else {
        ArtistsScreenContent(
            artist = artist,
            onNavigate = { navController.navigate(it) },
            onNavigationItemClicked = { index, item ->
                navController.navigate(item.navigateTo) {
                    viewModel.selectedItem = index
                    launchSingleTop = true
                }
            },
            selectedIndex = viewModel.selectedItem,
            chargePVMLists = {
                postViewModel.artistSongs(it)
                postViewModel.artistAlbums(it)
            },
            currentlyPlaying = viewModel.currentlyPlaying,
            onHandlePlayerActions = viewModel::handlePlayerActions,
            isPlaying = viewModel.isCurrentlyPlaying,
            animatedVisibilityScope = animatedVisibilityScope,
            isPlaylistEmpty = viewModel.isPlaylistEmpty()
        )
    }

}


@Composable
private fun SharedTransitionScope.ArtistsScreenContent(
    artist: List<Artist>,
    onNavigate: (Screen) -> Unit,
    chargePVMLists: (String) -> Unit,
    onNavigationItemClicked: (Int, NavigationItem) -> Unit,
    selectedIndex: Int,
    currentlyPlaying: String,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    isPlaying: Boolean,
    animatedVisibilityScope: AnimatedVisibilityScope,
    isPlaylistEmpty: Boolean
) {

    var query by remember { mutableStateOf("") }
    var screenSelectionExpanded by remember { mutableStateOf(false) }
    val displayArtists by remember(query) {
        derivedStateOf {
            if (query.isNotEmpty()) {
                artist.filter {
                    it.name.contains(
                        other = query,
                        ignoreCase = true
                    )
                }
            } else artist
        }
    }

    Scaffold { values ->

        Box {
            if (artist.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(values)
                ) {
                    CuteText(
                        text = stringResource(id = R.string.no_artists_found),
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,

                        )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(values),
                ) {
                    items(
                        items = displayArtists,
                        key = { it.id }
                    ) {
                        ArtistInfoList(it) {
                            chargePVMLists(it.name)
                            onNavigate(Screen.ArtistsDetails(it.id))
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
                        text = stringResource(id = R.string.search) + " " + stringResource(R.string.artists),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),

                        )
                },
                leadingIcon = {
                    IconButton(onClick = { screenSelectionExpanded = true }) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
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
                onHandlePlayerActions = onHandlePlayerActions,
                isPlaying = isPlaying,
                animatedVisibilityScope = animatedVisibilityScope,
                isPlaylistEmpty = isPlaylistEmpty
            )
        }
    }
}


@Composable
fun ArtistInfoList(
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
                CuteText(
                    text = artist.name,
                    maxLines = 1,
                    modifier = Modifier.then(
                        if (artist.name.length >= 25) {
                            Modifier.basicMarquee()
                        } else {
                            Modifier
                        }
                    )
                )

            }
        }
    }
}
