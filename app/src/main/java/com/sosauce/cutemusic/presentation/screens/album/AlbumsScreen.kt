@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.album

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberAlbumGrids
import com.sosauce.cutemusic.data.datastore.rememberAlbumSort
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.presentation.navigation.Screen
import com.sosauce.cutemusic.presentation.screens.album.components.AlbumCard
import com.sosauce.cutemusic.presentation.shared_components.CuteDropdownMenuItem
import com.sosauce.cutemusic.presentation.shared_components.CuteSearchbar
import com.sosauce.cutemusic.presentation.shared_components.NoResult
import com.sosauce.cutemusic.presentation.shared_components.NoXFound
import com.sosauce.cutemusic.presentation.shared_components.SortingDropdownMenu
import com.sosauce.cutemusic.utils.AlbumSort
import com.sosauce.cutemusic.utils.ordered

@Composable
fun SharedTransitionScope.AlbumsScreen(
    state: AlbumsState,
    musicState: MusicState,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
) {
    val textFieldState = rememberTextFieldState()
    var isSortedByASC by rememberSaveable { mutableStateOf(true) } // I prolly should change this
    var albumSort by rememberAlbumSort()
    val lazyState = rememberLazyGridState()
    var numberOfAlbumGrids by rememberAlbumGrids()

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ContainedLoadingIndicator()
        }
    } else {
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CuteSearchbar(
                        textFieldState = textFieldState,
                        showSearchField = true,
                        musicState = musicState,
                        sortingMenu = {
                            SortingDropdownMenu(
                                isSortedAscending = isSortedByASC,
                                onChangeSorting = { isSortedByASC = it },
                                topContent = {
                                    DropdownMenuItem(
                                        onClick = { numberOfAlbumGrids = if (numberOfAlbumGrids == 4) 2 else numberOfAlbumGrids + 1 },
                                        text = { Text(stringResource(R.string.no_of_grids)) },
                                        trailingIcon = { Text("$numberOfAlbumGrids") }
                                    )
                                }
                            ) {
                                repeat(2) { i ->
                                    val text = when (i) {
                                        0 -> R.string.title
                                        1 -> R.string.artist
                                        else -> throw IndexOutOfBoundsException()
                                    }
                                    DropdownMenuItem(
                                        selected = albumSort == i,
                                        onClick = { albumSort = i },
                                        shapes = MenuDefaults.itemShapes(),
                                        colors = MenuDefaults.selectableItemColors(),
                                        text = { Text(stringResource(text)) },
                                        trailingIcon = {
                                            if (albumSort == i) {
                                                Icon(
                                                    painter = painterResource(R.drawable.check),
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        },
                        onHandlePlayerActions = onHandlePlayerActions,
                        onNavigate = onNavigate
                    )
                }
            }
        ) { paddingValues ->
            val orderedAlbums = state.albums.ordered(
                sort = AlbumSort.entries[albumSort],
                ascending = isSortedByASC,
                query = textFieldState.text.toString()
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(if (orderedAlbums.isEmpty() || state.albums.isEmpty()) 1 else numberOfAlbumGrids),
                contentPadding = paddingValues,
                state = lazyState
            ) {
                if (state.albums.isEmpty()) {
                    item {
                        NoXFound(
                            headlineText = R.string.no_albums_found,
                            bodyText = R.string.no_album_desc,
                            icon = androidx.media3.session.R.drawable.media3_icon_album
                        )
                    }
                } else {
                    if (orderedAlbums.isEmpty()) {
                        item { NoResult() }
                    } else {
                        items(
                            items = orderedAlbums,
                            key = { it.id }
                        ) { album ->
                            AlbumCard(
                                modifier = Modifier.animateItem(),
                                album = album,
                                onClick = { onNavigate(Screen.AlbumsDetails(album.name)) }
                            )
                        }
                    }
                }
            }
        }
    }


}

