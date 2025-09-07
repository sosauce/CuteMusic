@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.presentation.screens.album

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberAlbumSort
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.model.Album
import com.sosauce.cutemusic.presentation.navigation.Screen
import com.sosauce.cutemusic.presentation.screens.album.components.AlbumCard
import com.sosauce.cutemusic.presentation.screens.main.components.SortingDropdownMenu
import com.sosauce.cutemusic.presentation.shared_components.CuteDropdownMenuItem
import com.sosauce.cutemusic.presentation.shared_components.CuteSearchbar
import com.sosauce.cutemusic.presentation.shared_components.CuteText
import com.sosauce.cutemusic.presentation.shared_components.NoResult
import com.sosauce.cutemusic.presentation.shared_components.NoXFound
import com.sosauce.cutemusic.utils.AlbumSort
import com.sosauce.cutemusic.utils.ordered
import com.sosauce.cutemusic.utils.rememberSearchbarAlignment
import com.sosauce.cutemusic.utils.showCuteSearchbar

@Composable
fun SharedTransitionScope.AlbumsScreen(
    albums: List<Album>,
    musicState: MusicState,
    numberOfAlbumGrids: Int,
    onChangeNumberOfGrids: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
) {
    val textFieldState = rememberTextFieldState()
    var isSortedByASC by rememberSaveable { mutableStateOf(true) } // I prolly should change this
    var albumSort by rememberAlbumSort()
    val state = rememberLazyGridState()

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Box {
            val orderedAlbums = albums.ordered(
                sort = AlbumSort.entries[albumSort],
                ascending = isSortedByASC,
                query = textFieldState.text.toString()
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(if (orderedAlbums.isEmpty() || albums.isEmpty()) 1 else numberOfAlbumGrids),
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = paddingValues,
                state = state
            ) {

                if (albums.isEmpty()) {
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
                                onClick = { onNavigate(Screen.AlbumsDetails(album.id)) }
                            )
                        }
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
                    textFieldState = textFieldState,
                    showSearchField = !state.isScrollInProgress,
                    musicState = musicState,
                    sortingMenu = {
                        SortingDropdownMenu(
                            isSortedByASC = isSortedByASC,
                            onChangeSorting = { isSortedByASC = it },
                            sortingOptions = {
                                CuteDropdownMenuItem(
                                    onClick = onChangeNumberOfGrids,
                                    text = { CuteText(stringResource(R.string.no_of_grids)) },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(R.drawable.grid_view),
                                            contentDescription = null
                                        )
                                    },
                                    trailingIcon = { CuteText(numberOfAlbumGrids.toString()) }
                                )
                                CuteDropdownMenuItem(
                                    onClick = { albumSort = 0 },
                                    text = { CuteText(stringResource(R.string.title)) },
                                    leadingIcon = {
                                        RadioButton(
                                            selected = albumSort == 0,
                                            onClick = null
                                        )
                                    }
                                )
                                CuteDropdownMenuItem(
                                    onClick = { albumSort = 1 },
                                    text = { CuteText(stringResource(R.string.artist)) },
                                    leadingIcon = {
                                        RadioButton(
                                            selected = albumSort == 1,
                                            onClick = null
                                        )
                                    }
                                )
                            }
                        )
                    },
                    onHandlePlayerActions = onHandlePlayerActions,
                    onNavigate = onNavigate
                )
            }
        }
    }
}

