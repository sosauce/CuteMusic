package com.sosauce.cutemusic.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FastForward
import androidx.compose.material.icons.outlined.FastRewind
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.ui.theme.GlobalFont

@Composable
fun MiniNowPlayingContent(
    onSeekNext: () -> Unit,
    onSeekPrevious: () -> Unit,
    onPlayOrPause: () -> Unit,
    viewModel: MusicViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(horizontal = 15.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (viewModel.title.length >= 25) viewModel.title.take(25) + "..." else if (viewModel.title == "null") viewModel.previousTitle else viewModel.title,
            fontFamily = GlobalFont,
            // modifier = Modifier.animateContentSize()
        )
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 4.dp)
        ) {
            IconButton(
                onClick = { onSeekPrevious() }
            ) {
                Icon(
                    imageVector = Icons.Outlined.FastRewind,
                    contentDescription = "previous song"
                )
            }
            IconButton(
                onClick = { onPlayOrPause() }
            ) {
                Icon(
                    imageVector = if (viewModel.isPlayerPlaying) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                    contentDescription = "play/pause button",
                    modifier = Modifier.animateContentSize(animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                    )
                )
            }
            IconButton(
                onClick = { onSeekNext() }
            ) {
                Icon(
                    imageVector = Icons.Outlined.FastForward,
                    contentDescription = "next song"
                )
            }
        }
    }
}