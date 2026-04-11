@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.album

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberAlbumGrids
import com.sosauce.chocola.data.datastore.rememberAlbumSort
import com.sosauce.chocola.data.datastore.rememberSortAlbumsAscending
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.navigation.Screen
import com.sosauce.chocola.presentation.screens.album.components.AlbumCard
import com.sosauce.chocola.presentation.shared_components.CuteSearchbar
import com.sosauce.chocola.presentation.shared_components.NoResult
import com.sosauce.chocola.presentation.shared_components.NoXFound
import com.sosauce.chocola.presentation.shared_components.SortingDropdownMenu
import com.sosauce.chocola.utils.selfAlignHorizontally

@Composable
fun SharedTransitionScope.AlbumsScreen(
    state: AlbumsState,
    musicState: MusicState,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
) {
    var isSortedByASC by rememberSortAlbumsAscending()
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
                CuteSearchbar(
                    modifier = Modifier.selfAlignHorizontally(),
                    textFieldState = state.textFieldState,
                    showSearchField = true,
                    musicState = musicState,
                    sortingMenu = {
                        SortingDropdownMenu(
                            isSortedAscending = isSortedByASC,
                            onChangeSorting = { isSortedByASC = it },
                            topContent = {
                                DropdownMenuItem(
                                    onClick = {
                                        numberOfAlbumGrids =
                                            if (numberOfAlbumGrids == 4) 2 else numberOfAlbumGrids + 1
                                    },
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
        ) { paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(if (state.albums.isEmpty()) 1 else numberOfAlbumGrids),
                contentPadding = paddingValues + PaddingValues(horizontal = 5.dp),
                state = lazyState,
            ) {
                if (state.albums.isEmpty() && !state.isSearching) {
                    item {
                        NoXFound(
                            headlineText = R.string.no_albums_found,
                            bodyText = R.string.no_album_desc,
                            icon = androidx.media3.session.R.drawable.media3_icon_album
                        )
                    }
                } else {
                    if (state.albums.isEmpty()) {
                        item { NoResult() }
                    } else {
                        itemsIndexed(
                            items = state.albums,
                            key = { _, album -> album.id }
                        ) { index, album ->
                            AlbumCard(
                                modifier = Modifier.animateContentSize().animateItem(),
                                shape = getAlbumCardShape(index, state.albums.size, numberOfAlbumGrids),
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

@Composable
private fun getAlbumCardShape(index: Int, totalItems: Int, columns: Int): Shape {
    val row = index / columns
    val col = index % columns
    val lastRow = (totalItems - 1) / columns
    val isLastRow = row == lastRow
    val isLastItem = index == totalItems - 1

    val cornerSize = 24.dp
    val flat = 0.dp

    return RoundedCornerShape(
        topStart = if (row == 0 && col == 0) cornerSize else flat,
        topEnd = if (row == 0 && (col == columns - 1 || isLastItem)) cornerSize else flat,
        bottomStart = if (isLastRow && col == 0) cornerSize else flat,
        bottomEnd = if (isLastItem || (isLastRow && col == columns - 1)) cornerSize else flat
    )
}

