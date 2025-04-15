@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.ui.screens.playing

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.actions.PlaylistActions
import com.sosauce.cutemusic.data.datastore.rememberSnapSpeedAndPitch
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.model.Lyrics
import com.sosauce.cutemusic.domain.model.Playlist
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.lyrics.LyricsView
import com.sosauce.cutemusic.ui.screens.playing.components.ActionButtonsRow
import com.sosauce.cutemusic.ui.screens.playing.components.Artwork
import com.sosauce.cutemusic.ui.screens.playing.components.CuteSlider
import com.sosauce.cutemusic.ui.screens.playing.components.QuickActionsRow
import com.sosauce.cutemusic.ui.screens.playing.components.SpeedCard
import com.sosauce.cutemusic.ui.screens.playlists.PlaylistItem
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicStateDetailsDialog
import com.sosauce.cutemusic.ui.shared_components.PlaylistViewModel
import com.sosauce.cutemusic.utils.ICON_TEXT_SPACING
import com.sosauce.cutemusic.utils.SharedTransitionKeys
import org.koin.androidx.compose.koinViewModel

@Composable
fun SharedTransitionScope.NowPlayingLandscape(
    onNavigateUp: () -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    musicState: MusicState,
    onChargeAlbumSongs: (String) -> Unit,
    onNavigate: (Screen) -> Unit,
    onChargeArtistLists: (String) -> Unit,
    lyrics: List<Lyrics>,
    animatedVisibilityScope: AnimatedVisibilityScope,
    loadedMedias: List<MediaItem> = emptyList()
) {
    val context = LocalContext.current
    var showSpeedCard by remember { mutableStateOf(false) }
    var showLyrics by remember { mutableStateOf(false) }
    var snap by rememberSnapSpeedAndPitch()
    var tempSliderValue by remember { mutableStateOf<Float?>(null) }
    var showPlaylistDialog by remember { mutableStateOf(false) }
    var showPlaylistCreatorDialog by remember { mutableStateOf(false) }

    var showDetailsDialog by remember { mutableStateOf(false) }

    if (showDetailsDialog) {
        MusicStateDetailsDialog(
            musicState = musicState,
            onDismissRequest = { showDetailsDialog = false }
        )
    }

    if (showPlaylistDialog) {
        val playlistViewModel = koinViewModel<PlaylistViewModel>()
        val playlists by playlistViewModel.allPlaylists.collectAsStateWithLifecycle()

        ModalBottomSheet(
            onDismissRequest = { showPlaylistDialog = false }
        ) {
            LazyColumn {
                item {
                    OutlinedButton(
                        onClick = { showPlaylistCreatorDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(ICON_TEXT_SPACING.dp))
                            CuteText(stringResource(R.string.create_playlist))
                        }
                    }
                }

                items(
                    items = playlists,
                    key = { it.id }
                ) { playlist ->
                    PlaylistItem(
                        playlist = playlist,
                        allowEditAction = false,
                        onClickPlaylist = {
                            if (playlist.musics.contains(musicState.mediaId)) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.alrdy_in_playlist),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val newPlaylist = Playlist(
                                    id = playlist.id,
                                    name = playlist.name,
                                    emoji = playlist.emoji,
                                    musics = playlist.musics.toMutableList()
                                        .apply { add(musicState.mediaId) }
                                )
                                playlistViewModel.handlePlaylistActions(
                                    PlaylistActions.UpsertPlaylist(newPlaylist)
                                )
                            }
                        }
                    )
                }
            }
        }

    }





    if (showSpeedCard) {
        SpeedCard(
            onDismissRequest = { showSpeedCard = false },
            shouldSnap = snap,
            onChangeSnap = { snap = !snap }
        )
    }

    val imgSize by animateDpAsState(
        targetValue = if (showLyrics) 200.dp else 320.dp,
        label = "Image Size"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .displayCutoutPadding()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column {
                Artwork(
                    pagerModifier = Modifier
                        .fillMaxWidth(0.4f),
                    musicState = musicState,
                    onHandlePlayerActions = onHandlePlayerActions,
                    loadedMedias = loadedMedias,
                    imageSize = imgSize,
                    animatedVisibilityScope = animatedVisibilityScope
                )
                if (showLyrics) {
                    Spacer(Modifier.height(10.dp))
                    CuteText(musicState.title)
                    CuteText(
                        text = musicState.artist,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.85f)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            AnimatedContent(showLyrics) { targetState ->
                if (targetState) {
                    LyricsView(
                        lyrics = lyrics,
                        onHideLyrics = { showLyrics = false },
                        musicState = musicState,
                        onHandlePlayerActions = onHandlePlayerActions
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(
                            onClick = { onNavigateUp() },
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp),
                            horizontalAlignment = Alignment.Start,
                        ) {
                            CuteText(
                                text = musicState.title,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 25.sp,
                                modifier = Modifier
                                    .sharedElement(
                                        state = rememberSharedContentState(key = SharedTransitionKeys.CURRENTLY_PLAYING),
                                        animatedVisibilityScope = animatedVisibilityScope
                                    )
                                    .basicMarquee()
                            )
                            CuteText(
                                text = musicState.artist,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.85f),
                                fontSize = 20.sp,
                                modifier = Modifier
                                    .sharedElement(
                                        state = rememberSharedContentState(key = SharedTransitionKeys.ARTIST + musicState.mediaId),
                                        animatedVisibilityScope = animatedVisibilityScope

                                    )
                                    .basicMarquee()
                            )
                        }
                        Spacer(Modifier.height(24.dp))
                        CuteSlider(
                            musicState = musicState,
                            onHandlePlayerActions = onHandlePlayerActions
                        )
                        ActionButtonsRow(
                            animatedVisibilityScope = animatedVisibilityScope,
                            musicState = musicState,
                            onHandlePlayerActions = onHandlePlayerActions
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        QuickActionsRow(
                            musicState = musicState,
                            onShowLyrics = { showLyrics = true },
                            onShowSpeedCard = { showSpeedCard = true },
                            onHandlePlayerActions = onHandlePlayerActions,
                            onNavigate = onNavigate,
                            onChargeAlbumSongs = onChargeAlbumSongs,
                            onChargeArtistLists = onChargeArtistLists
                        )
                    }
                }
            }

        }
    }
}