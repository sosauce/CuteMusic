@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.ui.screens.playing.components

import android.content.Context
import android.content.Intent
import android.media.audiofx.AudioEffect
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityOptionsCompat
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.actions.PlaylistActions
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.model.Playlist
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.playlists.CreatePlaylistDialog
import com.sosauce.cutemusic.ui.screens.playlists.PlaylistItem
import com.sosauce.cutemusic.ui.screens.playlists.PlaylistPicker
import com.sosauce.cutemusic.ui.shared_components.CuteDropdownMenuItem
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicStateDetailsDialog
import com.sosauce.cutemusic.ui.shared_components.PlaylistViewModel
import com.sosauce.cutemusic.utils.CUTE_MUSIC_ID
import com.sosauce.cutemusic.utils.ICON_TEXT_SPACING
import com.sosauce.cutemusic.utils.copyMutate
import org.koin.androidx.compose.koinViewModel

@Composable
fun QuickActionsRow(
    musicState: MusicState,
    onShowLyrics: () -> Unit,
    onShowSpeedCard: () -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onNavigate: (Screen) -> Unit = {},
) {
    val context = LocalContext.current
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val onBackground = MaterialTheme.colorScheme.onBackground
    var showPlaylistDialog by remember { mutableStateOf(false) }
    val uri = remember { musicState.uri.toUri() }


    if (showDetailsDialog) {
        MusicStateDetailsDialog(
            musicState = musicState,
            onDismissRequest = { showDetailsDialog = false }
        )
    }

    if (showTimePicker) {
        CuteTimePicker(
            onDismissRequest = { showTimePicker = false },
            onSetTimer = { hours, minutes ->
                showTimePicker = false
                onHandlePlayerActions(PlayerActions.SetSleepTimer(hours, minutes))
            }
        )
    }

    if (showPlaylistDialog) {
        PlaylistPicker(
            mediaId = listOf(musicState.mediaId),
            onDismissRequest = { showPlaylistDialog = false }
        )
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {

        IconButton(onClick = onShowLyrics) {
            Icon(
                painter = painterResource(R.drawable.lyrics_rounded),
                contentDescription = null
            )
        }
        IconButton(onClick = onShowSpeedCard) {
            Icon(
                painter = painterResource(R.drawable.speed_rounded),
                contentDescription = null

            )
        }
        ShuffleButton()
        LoopButton()
        IconButton(
            onClick = { showTimePicker = true }
        ) {
            Box {
                Icon(
                    painter = painterResource(R.drawable.bedtime_outlined),
                    contentDescription = null
                )
                if (musicState.sleepTimerActive) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .drawBehind { drawCircle(onBackground) }
                            .size(8.dp)
                    )
                }
            }
        }
        Column {

            var isDropDownExpanded by remember { mutableStateOf(false) }
            val eqIntent =
                rememberLauncherForActivityResult(equalizerActivityContract()) { }

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
                CuteDropdownMenuItem(
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
                CuteDropdownMenuItem(
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
                CuteDropdownMenuItem(
                    onClick = {
                        isDropDownExpanded = false
                        onNavigate(Screen.AlbumsDetails(musicState.albumId))
                    },
                    text = {
                        CuteText("${stringResource(R.string.go_to)} ${musicState.album}")
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(androidx.media3.session.R.drawable.media3_icon_album),
                            contentDescription = null
                        )
                    }
                )
                CuteDropdownMenuItem(
                    onClick = {
                        isDropDownExpanded = false
                        onNavigate(Screen.ArtistsDetails(musicState.artistId))
                    },
                    text = {
                        CuteText("${stringResource(R.string.go_to)} ${musicState.artist}")
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.artist_rounded),
                            contentDescription = null
                        )
                    }
                )
                CuteDropdownMenuItem(
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
                CuteDropdownMenuItem(
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
}

fun equalizerActivityContract() = object : ActivityResultContract<Unit, Unit>() {
    override fun createIntent(
        context: Context,
        input: Unit,
    ) = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL).apply {
        putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.packageName)
        putExtra(AudioEffect.EXTRA_AUDIO_SESSION, CUTE_MUSIC_ID)
        putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
    }

    override fun parseResult(
        resultCode: Int,
        intent: Intent?,
    ) {
    }
}
