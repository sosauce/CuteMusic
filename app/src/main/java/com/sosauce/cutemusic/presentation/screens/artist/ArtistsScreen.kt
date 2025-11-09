@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.artist

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberArtistSort
import com.sosauce.cutemusic.data.models.Artist
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.presentation.navigation.Screen
import com.sosauce.cutemusic.presentation.screens.main.components.SortingDropdownMenu
import com.sosauce.cutemusic.presentation.shared_components.CuteDropdownMenuItem
import com.sosauce.cutemusic.presentation.shared_components.CuteSearchbar
import com.sosauce.cutemusic.presentation.shared_components.NoResult
import com.sosauce.cutemusic.presentation.shared_components.NoXFound
import com.sosauce.cutemusic.utils.ArtistSort
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.ordered

@Composable
fun SharedTransitionScope.ArtistsScreen(
    state: ArtistsState,
    musicState: MusicState,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
) {

    val textFieldState = rememberTextFieldState()
    var isSortedByASC by rememberSaveable { mutableStateOf(true) }
    val lazyState = rememberLazyListState()
    var artistSort by rememberArtistSort()

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
                        musicState = musicState,
                        showSearchField = true,
                        sortingMenu = {
                            SortingDropdownMenu(
                                isSortedByASC = isSortedByASC,
                                onChangeSorting = { isSortedByASC = it },
                                sortingOptions = {

                                    repeat(3) { i ->
                                        val text = when (i) {
                                            0 -> R.string.name
                                            1 -> R.string.number_of_tracks
                                            2 -> R.string.number_of_albums
                                            else -> throw IndexOutOfBoundsException()
                                        }
                                        CuteDropdownMenuItem(
                                            onClick = { artistSort = i },
                                            text = { Text(stringResource(text)) },
                                            leadingIcon = {
                                                RadioButton(
                                                    selected = artistSort == i,
                                                    onClick = null
                                                )
                                            }
                                        )
                                    }
                                }
                            )
                        },
                        onHandlePlayerActions = onHandlePlayerActions,
                        onNavigate = onNavigate
                    )
                }
            }
        ) { paddingValues ->
            LazyColumn(
                contentPadding = paddingValues,
                state = lazyState
            ) {
                if (state.artists.isEmpty()) {
                    item {
                        NoXFound(
                            headlineText = R.string.no_artists_found,
                            bodyText = R.string.no_artists_found,
                            icon = R.drawable.artist_rounded
                        )
                    }
                } else {

                    val orderedArtists = state.artists.ordered(
                        sort = ArtistSort.entries[artistSort],
                        ascending = isSortedByASC,
                        query = textFieldState.text.toString()
                    )

                    if (orderedArtists.isEmpty()) {
                        item { NoResult() }
                    } else {
                        items(
                            items = orderedArtists,
                            key = { it.id }
                        ) { artist ->
                            Column(
                                modifier = Modifier
                                    .animateItem()
                                    .padding(
                                        vertical = 2.dp,
                                        horizontal = 4.dp
                                    )
                            ) {
                                ArtistItem(
                                    artist = artist,
                                    modifier = Modifier
                                        .padding(
                                            vertical = 2.dp,
                                            horizontal = 4.dp
                                        ),
                                    onClick = { onNavigate(Screen.ArtistsDetails(artist.name)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}


@Composable
fun SharedTransitionScope.ArtistItem(
    modifier: Modifier = Modifier,
    artist: Artist,
    onClick: () -> Unit
) {

    val context = LocalContext.current


    DropdownMenuItem(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        contentPadding = PaddingValues(0.dp),
        onClick = onClick,
        leadingIcon = {
            AsyncImage(
                model = ImageUtils.imageRequester(
                    ImageUtils.getAlbumArt(artist.albumId)
                        ?: androidx.media3.session.R.drawable.media3_icon_album,
                    context
                ),
                contentDescription = stringResource(R.string.artwork),
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(50.dp)
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = artist.id),
                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                    )
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop,
            )
        },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 15.dp)
            ) {
                Text(
                    text = artist.name,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    modifier = Modifier
                        .basicMarquee()
//                        .sharedBounds(
//                            sharedContentState = rememberSharedContentState(key = artist.name + artist.id),
//                            animatedVisibilityScope = LocalNavAnimatedContentScope.current,
//                        )
                )
                Text(
                    text = buildString {
                        append(
                            pluralStringResource(
                                R.plurals.tracks,
                                artist.numberTracks,
                                artist.numberTracks
                            )
                        )
                        if (artist.numberAlbums > 0) {
                            append(" & ")
                            append(
                                pluralStringResource(
                                    R.plurals.albums,
                                    artist.numberAlbums,
                                    artist.numberAlbums
                                )
                            )
                        }
                    },
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyLargeEmphasized.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    modifier = Modifier.basicMarquee()
                )

            }
        }
    )
}
