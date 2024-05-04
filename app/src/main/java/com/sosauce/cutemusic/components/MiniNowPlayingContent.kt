package com.sosauce.cutemusic.components

import android.util.Log
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
import com.sosauce.cutemusic.logic.MusicState
import com.sosauce.cutemusic.ui.theme.GlobalFont

@Composable
fun MiniNowPlayingContent(
    onSeekNext: () -> Unit,
    onSeekPrevious: () -> Unit,
    onPlayOrPause: () -> Unit,
    currentTitle: String,
    isPlaying: Boolean,
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
            text = if (currentTitle.length >= 25) currentTitle.take(25) + "..." else if (currentTitle == "null") viewModel.previousTitle else currentTitle,
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
                    imageVector = if (isPlaying) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                    contentDescription = "play/pause button"
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