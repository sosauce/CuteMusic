@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.album

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sosauce.cutemusic.R
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

@Composable
fun SharedTransitionScope.AlbumScreenLandscape(
    navController: NavController,
    albums: List<Album>,
    postViewModel: PostViewModel,
    viewModel: MusicViewModel,
    onNavigationItemClicked: (Int, NavigationItem) -> Unit,
    selectedIndex: Int,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigateTo: (Screen) -> Unit,
    currentlyPlaying: String,
    isCurrentlyPlaying: Boolean,
) {


    var sort by rememberSortASCAlbums()
    var query by remember { mutableStateOf("") }
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

        Box(Modifier.fillMaxSize()) {
            if (albums.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize()
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
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 30.dp)
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
                                    postViewModel.albumSongs(album.id)
                                    navController.navigate(Screen.AlbumsDetails(id = album.id))
                                }
                                .size(230.dp)
                        )
                    }
                }
            }

            CuteSearchbar(
                query = query,
                onQueryChange = { query = it },
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth(0.4f)
                    .padding(
                        bottom = 5.dp,
                        end = 10.dp
                    )
                    .align(Alignment.BottomEnd)
                    .sharedElement(
                        state = rememberSharedContentState(key = "searchbar"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ ->
                            tween(durationMillis = 500)
                        }
                    ),
                placeholder = {
                    CuteText(
                        text = stringResource(id = R.string.search) + " " + stringResource(id = R.string.music),
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
                            onClick = { onNavigateTo(Screen.Settings) }
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
                onHandlePlayerActions = { viewModel.handlePlayerActions(it) },
                isPlaying = isCurrentlyPlaying,
                animatedVisibilityScope = animatedVisibilityScope,
                isPlaylistEmpty = viewModel.isPlaylistEmptyAndDataNotNull(),
                onNavigate = { onNavigateTo(Screen.NowPlaying) }
            )
        }
}
