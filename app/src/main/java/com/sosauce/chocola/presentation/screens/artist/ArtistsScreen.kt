@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.artist

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import coil3.compose.AsyncImage
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberArtistSort
import com.sosauce.chocola.data.datastore.rememberSortArtistsAscending
import com.sosauce.chocola.data.models.Artist
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.navigation.Screen
import com.sosauce.chocola.presentation.shared_components.CuteListItem
import com.sosauce.chocola.presentation.shared_components.CuteSearchbar
import com.sosauce.chocola.presentation.shared_components.NoResult
import com.sosauce.chocola.presentation.shared_components.NoXFound
import com.sosauce.chocola.presentation.shared_components.SortingDropdownMenu
import com.sosauce.chocola.utils.ArtistSort
import com.sosauce.chocola.utils.ImageUtils
import com.sosauce.chocola.utils.ordered
import com.sosauce.chocola.utils.selfAlignHorizontally
import sv.lib.squircleshape.CornerSmoothing
import sv.lib.squircleshape.SquircleShape

@Composable
fun SharedTransitionScope.ArtistsScreen(
    state: ArtistsState,
    musicState: MusicState,
    onNavigate: (Screen) -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
) {

    val lazyState = rememberLazyListState()
    var artistSort by rememberArtistSort()
    var isSortedByASC by rememberSortArtistsAscending()

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
                    musicState = musicState,
                    showSearchField = true,
                    sortingMenu = {
                        SortingDropdownMenu(
                            isSortedAscending = isSortedByASC,
                            onChangeSorting = { isSortedByASC = it },
                        ) {
                            repeat(3) { i ->
                                val text = when (i) {
                                    0 -> R.string.name
                                    1 -> R.string.number_of_tracks
                                    2 -> R.string.number_of_albums
                                    else -> throw IndexOutOfBoundsException()
                                }

                                DropdownMenuItem(
                                    selected = artistSort == i,
                                    onClick = { artistSort = i },
                                    shapes = MenuDefaults.itemShapes(),
                                    colors = MenuDefaults.selectableItemColors(),
                                    text = { Text(stringResource(text)) },
                                    trailingIcon = {
                                        if (artistSort == i) {
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
            LazyColumn(
                contentPadding = paddingValues,
                state = lazyState
            ) {
                if (state.artists.isEmpty() && !state.isSearching) {
                    item {
                        NoXFound(
                            headlineText = R.string.no_artists_found,
                            bodyText = R.string.no_artist_desc,
                            icon = R.drawable.artist_rounded
                        )
                    }
                } else {

                    if (state.artists.isEmpty()) {
                        item { NoResult() }
                    } else {
                        items(
                            items = state.artists,
                            key = { it.id }
                        ) { artist ->
                            ArtistItem(
                                modifier = Modifier.animateItem(),
                                artist = artist,
                                onClick = { onNavigate(Screen.ArtistsDetails(artist.name)) }
                            )
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

    CuteListItem(
        modifier = modifier,
        onClick = onClick,
        leadingContent = {
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
                    .clip(SquircleShape(smoothing = CornerSmoothing.Full)),
                contentScale = ContentScale.Crop,
            )
        }
    ) {
        Text(
            text = artist.name,
            maxLines = 1,
            style = MaterialTheme.typography.titleMediumEmphasized,
            modifier = Modifier.basicMarquee()
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
