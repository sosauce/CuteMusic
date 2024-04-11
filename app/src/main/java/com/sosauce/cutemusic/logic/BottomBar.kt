package com.sosauce.cutemusic.logic

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.ui.theme.GlobalFont

@Composable
fun BottomBar(
    onSeekNext: () -> Unit,
    onSeekPrevious: () -> Unit,
    onPlayOrPause: () -> Unit,
    viewModel: MusicViewModel,
    navController: NavController
) {

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("NowPlaying") },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = viewModel.title,
                fontFamily = GlobalFont,
                modifier = Modifier.padding(start = 10.dp)
            )
            Row(horizontalArrangement = Arrangement.End) {
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
}