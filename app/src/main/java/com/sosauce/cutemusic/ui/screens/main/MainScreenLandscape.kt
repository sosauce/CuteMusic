@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.main

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.MusicNote
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberSortASC
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.shared_components.CuteSearchbar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.shared_components.NavigationItem
import com.sosauce.cutemusic.ui.shared_components.ScreenSelection
import com.sosauce.cutemusic.ui.shared_components.SortRadioButtons

@Composable
fun SharedTransitionScope.MainScreenLandscape(
    musics: List<MediaItem>,
    viewModel: MusicViewModel,
    onShortClick: (String) -> Unit,
    onNavigationItemClicked: (Int, NavigationItem) -> Unit,
    selectedIndex: Int,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigateTo: (Screen) -> Unit,
    currentlyPlaying: String,
    isCurrentlyPlaying: Boolean,
) {

    val sort by rememberSortASC()
    var query by remember { mutableStateOf("") }
    var sortExpanded by remember { mutableStateOf(false) }
    var screenSelectionExpanded by remember { mutableStateOf(false) }
    val displayMusics by remember(sort, musics, query) {
        derivedStateOf {
            if (query.isNotEmpty()) {
                musics.filter {
                    it.mediaMetadata.title?.contains(
                        other = query,
                        ignoreCase = true
                    ) == true
                }
            } else {
                if (sort) musics
                else musics.sortedByDescending { it.mediaMetadata.title.toString() }
            }

        }
    }


    Scaffold { values ->
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(values)
                    .padding(start = 80.dp),
                verticalArrangement = Arrangement.Top
            ) {
                items(displayMusics) { music ->
                    MusicListItem(
                        onShortClick = { onShortClick(music.mediaId) },
                        music = music,
                        onNavigate = { onNavigateTo(it) },
                        currentMusicUri = viewModel.currentMusicUri

                    )
                }
            }

            CuteSearchbar(
                query = query,
                onQueryChange = { query = it },
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth(0.4f)
                    .padding(
                        bottom = values.calculateBottomPadding() + 5.dp,
                        end = values.calculateEndPadding(
                            layoutDirection = LayoutDirection.Rtl
                        ) + 10.dp
                    )
                    .align(Alignment.BottomEnd)
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
                    .clickable { onNavigateTo(Screen.NowPlaying) }
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
                            imageVector = Icons.Rounded.MusicNote,
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
                            SortRadioButtons()
                        }
                    }
                },
                currentlyPlaying = currentlyPlaying,
                onHandlePlayerActions = { viewModel.handlePlayerActions(it) },
                isPlaying = isCurrentlyPlaying,
                animatedVisibilityScope = animatedVisibilityScope,
                isPlaylistEmpty = viewModel.isPlaylistEmpty()
            )

        }
    }
}