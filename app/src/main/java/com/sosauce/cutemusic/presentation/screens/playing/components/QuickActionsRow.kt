@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.playing.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.ButtonGroup
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.presentation.screens.playlists.components.PlaylistPicker
import com.sosauce.cutemusic.presentation.shared_components.MusicStateDetailsDialog
import com.sosauce.cutemusic.utils.rememberInteractionSource
import com.sosauce.cutemusic.utils.selfAlignHorizontally


@Composable
fun QuickActionsRow(
    musicState: MusicState,
    onShowLyrics: () -> Unit,
    onShowSpeedCard: () -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showPlaylistDialog by remember { mutableStateOf(false) }
    var showQueueSheet by remember { mutableStateOf(false) }
    val interactionSources = List(6) { rememberInteractionSource() }


    if (showQueueSheet) {
        QueueSheet(
            onDismissRequest = { showQueueSheet = false },
            onHandlePlayerAction = onHandlePlayerActions,
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
        modifier = Modifier.selfAlignHorizontally(),
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
            onCheckedChange = { onHandlePlayerActions(PlayerActions.ChangeRepeatMode) },
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
    }
}
