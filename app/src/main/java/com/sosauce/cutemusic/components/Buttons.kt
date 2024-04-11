package com.sosauce.cutemusic.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Loop
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.media3.common.Player
import com.sosauce.cutemusic.activities.MusicViewModel

@Composable
fun LoopButton(
    player: Player,
    viewModel: MusicViewModel
) {
    IconButton(
        onClick = { if (player.repeatMode == Player.REPEAT_MODE_ONE) player.repeatMode = Player.REPEAT_MODE_OFF else player.repeatMode = Player.REPEAT_MODE_ONE }
    ) {
        Icon(
            imageVector = Icons.Outlined.Loop,
            contentDescription = "loop button",
            tint = viewModel.iconTint()
        )
    }
}

@Composable
fun ShuffleButton(
    player: Player,
    viewModel: MusicViewModel
) {
    IconButton(
        onClick = { player.shuffleModeEnabled = !player.shuffleModeEnabled }
    ) {
        Icon(
            imageVector = Icons.Outlined.Shuffle,
            contentDescription = "shuffle button",
            tint = viewModel.shuffleIconTint()
        )
    }
}