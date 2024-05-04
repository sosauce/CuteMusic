package com.sosauce.cutemusic.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Loop
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.media3.common.Player

@Composable
fun LoopButton(
    player: Player,
    onClick: () -> Unit
) {
    IconButton(
        onClick = { onClick() }
    ) {
        Icon(
            imageVector = Icons.Outlined.Loop,
            contentDescription = "loop button",
            tint = if (player.repeatMode == Player.REPEAT_MODE_ONE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun ShuffleButton(
    onShuffle: () -> Unit,
    player: Player
) {
    IconButton(
        onClick = { onShuffle() }
    ) {
        Icon(
            imageVector = Icons.Outlined.Shuffle,
            contentDescription = "shuffle button",
            tint = if (player.shuffleModeEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
        )
    }
}