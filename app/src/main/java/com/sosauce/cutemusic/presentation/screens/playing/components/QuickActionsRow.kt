@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.playing.components

import android.content.Context
import android.content.Intent
import android.media.audiofx.AudioEffect
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.presentation.navigation.Screen
import com.sosauce.cutemusic.presentation.screens.playlists.components.PlaylistPicker
import com.sosauce.cutemusic.presentation.shared_components.CuteDropdownMenuItem
import com.sosauce.cutemusic.presentation.shared_components.CuteText
import com.sosauce.cutemusic.presentation.shared_components.MusicStateDetailsDialog
import com.sosauce.cutemusic.utils.CUTE_MUSIC_ID
import com.sosauce.cutemusic.utils.rememberInteractionSource


@Composable
fun QuickActionsRow(
    musicState: MusicState,
    onShowLyrics: () -> Unit,
    onShowSpeedCard: () -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onNavigate: (Screen) -> Unit = {},
    loadedMedias: List<MediaItem>
) {
    val context = LocalContext.current
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showMoreDialog by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showPlaylistDialog by remember { mutableStateOf(false) }
    val uri = remember { musicState.uri.toUri() }
    var showQueueSheet by remember { mutableStateOf(false) }
    val eqIntent = rememberLauncherForActivityResult(equalizerActivityContract()) { }
    val interactionSources = List(7) { rememberInteractionSource() }


    if (showQueueSheet) {
        QueueSheet(
            onDismissRequest = { showQueueSheet = false },
            onHandlePlayerAction = onHandlePlayerActions,
            loadedMedias = loadedMedias,
            musicState = musicState
        )
    }


    if (showDetailsDialog) {
        MusicStateDetailsDialog(
            musicState = musicState,
            onDismissRequest = { showDetailsDialog = false }
        )
    }

    if (showTimePicker) {
        CuteTimePicker(
            initialMillis = musicState.sleepTimerRemainingDuration,
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

    ButtonGroup(
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        IconButton(
            onClick = onShowLyrics,
            interactionSource = interactionSources[0],
            modifier = Modifier
                .animateWidth(interactionSources[0])
        ) {
            Icon(
                painter = painterResource(R.drawable.lyrics_filled),
                contentDescription = null
            )
        }
        IconButton(
            onClick = onShowSpeedCard,
            interactionSource = interactionSources[1],
            modifier = Modifier
                .animateWidth(interactionSources[1])
        ) {
            Icon(
                painter = painterResource(R.drawable.speed_filled),
                contentDescription = null

            )
        }
        ToggleButton(
            checked = musicState.shuffle,
            onCheckedChange = { onHandlePlayerActions(PlayerActions.Shuffle) },
            interactionSource = interactionSources[2],
            colors = ToggleButtonDefaults.toggleButtonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .animateWidth(interactionSources[2])
        ) {
            Icon(
                imageVector = Icons.Rounded.Shuffle,
                contentDescription = "shuffle button"
            )
        }
        ToggleButton(
            checked = musicState.repeatMode != Player.REPEAT_MODE_OFF,
            onCheckedChange = {

                val repeatMode = when (musicState.repeatMode) {
                    Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                    Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                    else -> Player.REPEAT_MODE_OFF
                }

                onHandlePlayerActions(PlayerActions.RepeatMode(repeatMode))
            },
            interactionSource = interactionSources[3],
            colors = ToggleButtonDefaults.toggleButtonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .animateWidth(interactionSources[3])
        ) {

            val icon = when (musicState.repeatMode) {
                Player.REPEAT_MODE_OFF, Player.REPEAT_MODE_ALL -> R.drawable.repeat
                else -> R.drawable.repeat_one
            }

            Icon(
                painter = painterResource(icon),
                contentDescription = "repeat mode"
            )
        }
        IconButton(
            onClick = { showQueueSheet = true },
            interactionSource = interactionSources[4],
            modifier = Modifier
                .animateWidth(interactionSources[4])
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.PlaylistPlay,
                contentDescription = null
            )
        }
        IconButton(
            onClick = { showTimePicker = true },
            interactionSource = interactionSources[5],
            modifier = Modifier
                .animateWidth(interactionSources[5])
        ) {
            Icon(
                painter = painterResource(
                    if (musicState.sleepTimerRemainingDuration != 0L) {
                        R.drawable.sleep_timer_active_filled
                    } else {
                        R.drawable.sleep_timer_filled
                    }
                ),
                contentDescription = null
            )
        }
        Column {
            IconButton(
                onClick = { showMoreDialog = true },
                interactionSource = interactionSources[6],
                modifier = Modifier
                    .animateWidth(interactionSources[6])
            ) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = null
                )
            }

            DropdownMenu(
                expanded = showMoreDialog,
                onDismissRequest = { showMoreDialog = false },
                shape = RoundedCornerShape(24.dp)
            ) {
                CuteDropdownMenuItem(
                    onClick = {
                        try {
                            eqIntent.launch()
                        } catch (e: Exception) {
                            e.stackTrace
                        }
                    },
                    text = {
                        CuteText(stringResource(R.string.open_eq))
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.eq),
                            contentDescription = null
                        )
                    }
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
                        showMoreDialog = false
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
                        showMoreDialog = false
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
