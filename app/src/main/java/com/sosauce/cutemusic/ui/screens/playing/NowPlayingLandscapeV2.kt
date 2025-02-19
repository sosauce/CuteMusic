@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.ui.screens.playing

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.actions.PlaylistActions
import com.sosauce.cutemusic.data.datastore.rememberSnapSpeedAndPitch
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.model.Lyrics
import com.sosauce.cutemusic.domain.model.Playlist
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.lyrics.LyricsView
import com.sosauce.cutemusic.ui.screens.playing.components.ActionButtonsRowV2
import com.sosauce.cutemusic.ui.screens.playing.components.ActionsButtonsRow
import com.sosauce.cutemusic.ui.screens.playing.components.MusicSlider
import com.sosauce.cutemusic.ui.screens.playing.components.QuickActionsRowV2
import com.sosauce.cutemusic.ui.screens.playing.components.SpeedCard
import com.sosauce.cutemusic.ui.screens.playing.components.equalizerActivityContract
import com.sosauce.cutemusic.ui.screens.playlists.PlaylistItem
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicStateDetailsDialog
import com.sosauce.cutemusic.ui.shared_components.PlaylistViewModel
import com.sosauce.cutemusic.utils.ICON_TEXT_SPACING
import com.sosauce.cutemusic.utils.formatToReadableTime
import me.saket.squiggles.SquigglySlider
import org.koin.androidx.compose.koinViewModel

@Composable
fun NowPlayingLandscapeV2(
    onNavigateUp: () -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    musicState: MusicState,
    onChargeAlbumSongs: (String) -> Unit,
    onNavigate: (Screen) -> Unit,
    onChargeArtistLists: (String) -> Unit,
    lyrics: List<Lyrics>
) {
    val context = LocalContext.current
    var showSpeedCard by remember { mutableStateOf(false) }
    var showLyrics by remember { mutableStateOf(false) }
    var snap by rememberSnapSpeedAndPitch()
    var tempSliderValue by remember { mutableStateOf<Float?>(null) }
    val uri = remember { Uri.parse(musicState.currentMusicUri) }
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
                            if (playlist.musics.contains(musicState.currentMediaId)) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.alrdy_in_playlist),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val playlist = Playlist(
                                    id = playlist.id,
                                    name = playlist.name,
                                    emoji = playlist.emoji,
                                    musics = playlist.musics.toMutableList()
                                        .apply { add(musicState.currentMediaId) }
                                )
                                playlistViewModel.handlePlaylistActions(
                                    PlaylistActions.UpsertPlaylist(playlist)
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
            onDismiss = { showSpeedCard = false },
            shouldSnap = snap,
            onChangeSnap = { snap = !snap },
            musicState = musicState,
            onHandlePlayerAction = onHandlePlayerActions
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
                AsyncImage(
                    model = musicState.currentArt,
                    stringResource(R.string.artwork),
                    modifier = Modifier
                        .size(imgSize)
                        .clip(RoundedCornerShape(10)),
                    contentScale = ContentScale.Crop
                )
                if (showLyrics) {
                    Spacer(Modifier.height(10.dp))
                    CuteText(musicState.currentlyPlaying)
                    CuteText(
                        text = musicState.currentArtist,
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = { onNavigateUp() }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.KeyboardArrowDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Column {

                                var isDropDownExpanded by remember { mutableStateOf(false) }
                                val eqIntent = rememberLauncherForActivityResult(equalizerActivityContract()) { }
                                var showDetailsDialog by remember { mutableStateOf(false) }

                                IconButton(onClick = { isDropDownExpanded = true }) {
                                    Icon(
                                        imageVector = Icons.Rounded.MoreVert,
                                        contentDescription = "more"
                                    )
                                }


                                DropdownMenu(
                                    expanded = isDropDownExpanded,
                                    onDismissRequest = { isDropDownExpanded = false },
                                    shape = RoundedCornerShape(24.dp)
                                ) {
                                    DropdownMenuItem(
                                        onClick = {
                                            try {
                                                eqIntent.launch()
                                            } catch (e: Exception) {
                                                Log.d(
                                                    "CuteError",
                                                    "Couldn't open EQ: ${e.stackTrace}, ${e.message}"
                                                )
                                            }
                                        },
                                        text = {
                                            CuteText(stringResource(R.string.open_eq))
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                                                contentDescription = null
                                            )
                                        }
                                    )
                                    HorizontalDivider(
                                        modifier = Modifier
                                            .fillMaxWidth(0.9f)
                                            .align(Alignment.CenterHorizontally)
                                    )
                                    DropdownMenuItem(
                                        onClick = { showDetailsDialog = true },
                                        text = {
                                            CuteText(stringResource(R.string.details))
                                        },
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.info_rounded),
                                                contentDescription = null
                                            )
                                        }
                                    )
                                    DropdownMenuItem(
                                        onClick = {
                                            isDropDownExpanded = false
                                            onChargeAlbumSongs(musicState.currentAlbum)
                                            onNavigate(Screen.AlbumsDetails(musicState.currentAlbumId))
                                        },
                                        text = {
                                            CuteText("${stringResource(R.string.go_to)} ${musicState.currentAlbum}")
                                        },
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(androidx.media3.session.R.drawable.media3_icon_album),
                                                contentDescription = null
                                            )
                                        }
                                    )
                                    DropdownMenuItem(
                                        onClick = {
                                            isDropDownExpanded = false
                                            onChargeArtistLists(musicState.currentArtist)
                                            onNavigate(Screen.ArtistsDetails(musicState.currentArtistId))
                                        },
                                        text = {
                                            CuteText("${stringResource(R.string.go_to)} ${musicState.currentArtist}")
                                        },
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(R.drawable.artist_rounded),
                                                contentDescription = null
                                            )
                                        }
                                    )
                                    DropdownMenuItem(
                                        onClick = { showPlaylistDialog = true },
                                        text = {
                                            CuteText(stringResource(R.string.add_to_playlist))
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Rounded.PlaylistAdd,
                                                contentDescription = null
                                            )
                                        }
                                    )
                                    DropdownMenuItem(
                                        onClick = {
                                            context.startActivity(
                                                Intent.createChooser(
                                                    Intent().apply {
                                                        action = Intent.ACTION_SEND
                                                        putExtra(Intent.EXTRA_STREAM, uri)
                                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                        type = "audio/*"
                                                    }, null
                                                )
                                            )
                                        },
                                        text = {
                                            CuteText(
                                                text = stringResource(R.string.share)
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(androidx.media3.session.R.drawable.media3_icon_share),
                                                contentDescription = null
                                            )
                                        }
                                    )
                                }
                            }
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp),
                            horizontalAlignment = Alignment.Start,
                        ) {
                            CuteText(
                                text = musicState.currentlyPlaying,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 25.sp,
                                modifier = Modifier
                                    .basicMarquee()
                            )
                            CuteText(
                                text = musicState.currentArtist,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.85f),
                                fontSize = 20.sp,
                                modifier = Modifier.basicMarquee()
                            )
                        }
                        Spacer(Modifier.height(24.dp))
                        Column(
                            modifier = Modifier.padding(horizontal = 15.dp)
                        ) {
                            Row (
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                CuteText(
                                    text = musicState.currentPosition.formatToReadableTime(),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                CuteText(
                                    text = musicState.currentMusicDuration.formatToReadableTime(),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Slider(
                                value = tempSliderValue ?: musicState.currentPosition.toFloat(),
                                onValueChange = { tempSliderValue = it },
                                onValueChangeFinished = {
                                    tempSliderValue?.let {
                                        onHandlePlayerActions(
                                            PlayerActions.UpdateCurrentPosition(it.toLong())
                                        )
                                        onHandlePlayerActions(
                                            PlayerActions.SeekToSlider(it.toLong())
                                        )
                                    }
                                    tempSliderValue = null
                                },
                                track = { sliderState ->

                                    val amplitude by animateDpAsState(
                                        targetValue = if (musicState.isCurrentlyPlaying) 5.dp else 0.dp
                                    )
                                    SquigglySlider.Track(
                                        interactionSource = remember { MutableInteractionSource() },
                                        colors = SliderDefaults.colors(),
                                        enabled = true,
                                        sliderState = sliderState,
                                        squigglesSpec = SquigglySlider.SquigglesSpec(
                                            amplitude = amplitude,
                                            wavelength = 45.dp
                                        )
                                    )
                                },
                                thumb = { sliderState ->
                                    Spacer(Modifier.width(4.dp))
                                    SliderDefaults.Thumb(
                                        interactionSource = remember { MutableInteractionSource() },
                                        thumbSize = DpSize(width = 4.dp, height = 22.dp),
                                    )
                                    Spacer(Modifier.width(4.dp))
                                },
                                valueRange = 0f..musicState.currentMusicDuration.toFloat(),
                            )
                        }
                        ActionButtonsRowV2(
                            musicState = musicState,
                            onHandlePlayerActions = onHandlePlayerActions
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        QuickActionsRowV2(
                            musicState = musicState,
                            onShowLyrics = { showLyrics = true },
                            onShowSpeedCard = { showSpeedCard = true },
                            onHandlePlayerActions = onHandlePlayerActions,
                        )
                    }
                }
            }

        }
    }
}