@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.ui.screens.playing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberSnapSpeedAndPitch
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel

@Composable
fun SpeedCard(
    onDismiss: () -> Unit,
    viewModel: MusicViewModel
) {
    var speed by remember { mutableFloatStateOf(viewModel.getPlaybackSpeed().speed) }
    var pitch by remember { mutableFloatStateOf(viewModel.getPlaybackSpeed().pitch) }
    val interactionSource = remember {
        MutableInteractionSource()
    }
    var snap by rememberSnapSpeedAndPitch()

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                CuteText(
                    text = stringResource(id = R.string.okay),

                    )
            }
        },
        title = {
            CuteText(
                text = stringResource(id = R.string.playback_speed),

                )
        },
        text = {
            Box {
                Column {
                    Spacer(Modifier.height(5.dp))
                    if (!snap) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            CuteText(
                                text = "Speed: " + "%.2f".format(speed),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 15.sp
                            )
                        }
                        Slider(
                            value = speed,
                            onValueChange = {
                                speed = it
                                viewModel.setPlaybackSpeed(speed, pitch)
                            },
                            valueRange = 0.5f..2f,
                            track = { sliderState ->
                                SliderDefaults.Track(
                                    sliderState = sliderState,
                                    drawStopIndicator = null,
                                    thumbTrackGapSize = 4.dp,
                                    modifier = Modifier.height(8.dp)
                                )
                            },
                            thumb = {
                                SliderDefaults.Thumb(
                                    interactionSource = interactionSource,
                                    thumbSize = DpSize(width = 4.dp, height = 25.dp)
                                )
                            },
                            interactionSource = interactionSource
                        )
                        Spacer(Modifier.height(15.dp))
                        //
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            CuteText(
                                text = "Pitch: " + "%.2f".format(pitch),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Slider(
                            value = pitch,
                            onValueChange = {
                                pitch = it
                                viewModel.setPlaybackSpeed(speed, pitch)
                            },
                            valueRange = 0.5f..2f,
                            track = { sliderState ->
                                SliderDefaults.Track(
                                    sliderState = sliderState,
                                    drawStopIndicator = null,
                                    thumbTrackGapSize = 4.dp,
                                    modifier = Modifier.height(8.dp)
                                )
                            },
                            thumb = {
                                SliderDefaults.Thumb(
                                    interactionSource = interactionSource,
                                    thumbSize = DpSize(width = 4.dp, height = 25.dp)
                                )
                            },
                            interactionSource = interactionSource
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Slider(
                                value = speed,
                                onValueChange = {
                                    speed = it
                                    pitch = it
                                    viewModel.setPlaybackSpeed(speed, pitch)
                                },
                                valueRange = 0.5f..2f,
                                track = { sliderState ->
                                    SliderDefaults.Track(
                                        sliderState = sliderState,
                                        drawStopIndicator = null,
                                        thumbTrackGapSize = 4.dp,
                                        modifier = Modifier.height(8.dp)
                                    )
                                },
                                thumb = {
                                    SliderDefaults.Thumb(
                                        interactionSource = interactionSource,
                                        thumbSize = DpSize(width = 4.dp, height = 25.dp)
                                    )
                                },
                                interactionSource = interactionSource
                            )
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            ) {
                                CuteText(
                                    text = "%.2f".format(speed),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Checkbox(
                            checked = snap,
                            onCheckedChange = { snap = !snap }
                        )
                        CuteText(
                            text = "Snap speed and pitch"
                        )
                    }
                }
            }
        }
    )
}
