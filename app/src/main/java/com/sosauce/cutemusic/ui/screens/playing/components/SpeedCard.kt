@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.ui.screens.playing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.theme.GlobalFont

@Composable
fun SpeedCard(
    onDismiss: () -> Unit,
    viewModel: MusicViewModel
) {
    var speed by remember { mutableFloatStateOf(viewModel.getPlaybackSpeed().speed) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(
                    text = stringResource(id = R.string.okay),
                    fontFamily = GlobalFont
                )
            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.playback_speed),
                fontFamily = GlobalFont
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Slider(
                    value = speed,
                    onValueChange = {
                        speed = it
                        viewModel.setPlaybackSpeed(speed)
                    },
                    valueRange = 0.5f..2f
                )
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "%.2f".format(speed),
                        fontFamily = GlobalFont,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    )
}
