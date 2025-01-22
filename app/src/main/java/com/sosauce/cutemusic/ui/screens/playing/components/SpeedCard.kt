@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.ui.screens.playing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.ui.shared_components.CuteText

@Composable
fun SpeedCard(
    onDismiss: () -> Unit,
    shouldSnap: Boolean,
    onChangeSnap: () -> Unit,
    musicState: MusicState,
    onHandlePlayerAction: (PlayerActions) -> Unit
) {
    var speed by remember { mutableFloatStateOf(musicState.playbackParameters.speed) }
    var pitch by remember { mutableFloatStateOf(musicState.playbackParameters.pitch) }
    var showSpeedAndPitchChangerDialog by remember { mutableStateOf(false) }
    var showSpeedChangerDialog by remember { mutableStateOf(false) }
    var showPitchChangerDialog by remember { mutableStateOf(false) }
    val interactionSource = remember {
        MutableInteractionSource()
    }

    if (showSpeedAndPitchChangerDialog) {
        RateAdjustmentDialog(
            rate = speed,
            onSetNewRate = {
                speed = it
                pitch = it
                onHandlePlayerAction(
                    PlayerActions.ApplyPlaybackSpeed(
                        speed = speed,
                        pitch = pitch
                    )
                )
                showSpeedAndPitchChangerDialog = false
            },
            titleText = stringResource(id = R.string.set_sap),
            onDismissRequest = { showSpeedAndPitchChangerDialog = false }
        )
    }

    if (showSpeedChangerDialog) {
        RateAdjustmentDialog(
            rate = speed,
            onSetNewRate = {
                speed = it
                onHandlePlayerAction(
                    PlayerActions.ApplyPlaybackSpeed(
                        speed = speed,
                        pitch = pitch
                    )
                )
                showSpeedChangerDialog = false
            },
            titleText = stringResource(id = R.string.set_speed),
            onDismissRequest = { showSpeedChangerDialog = false })
    }

    if (showPitchChangerDialog) {
        RateAdjustmentDialog(
            rate = pitch,
            onSetNewRate = {
                speed = it
                onHandlePlayerAction(
                    PlayerActions.ApplyPlaybackSpeed(
                        speed = speed,
                        pitch = pitch
                    )
                )
                showPitchChangerDialog = false
            },
            titleText = stringResource(id = R.string.set_pitch),
            onDismissRequest = { showPitchChangerDialog = false }
        )
    }


    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                CuteText(text = stringResource(id = R.string.okay))
            }
        },
        title = { CuteText(stringResource(id = R.string.playback_speed)) },
        text = {
            Box {
                Column {
                    Spacer(Modifier.height(5.dp))
                    if (!shouldSnap) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(11.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                .clickable { showSpeedChangerDialog = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            CuteText(
                                text = "${stringResource(id = R.string.speed)}: " + "%.2f".format(
                                    speed
                                ),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 15.sp
                            )
                        }
                        Slider(
                            value = speed,
                            onValueChange = {
                                speed = it
                                onHandlePlayerAction(
                                    PlayerActions.ApplyPlaybackSpeed(
                                        speed = speed,
                                        pitch = pitch
                                    )
                                )
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
                                .clip(RoundedCornerShape(11.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                .clickable { showPitchChangerDialog = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            CuteText(
                                text = "${stringResource(id = R.string.pitch)}: " + "%.2f".format(
                                    pitch
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Slider(
                            value = pitch,
                            onValueChange = {
                                pitch = it
                                onHandlePlayerAction(
                                    PlayerActions.ApplyPlaybackSpeed(
                                        speed = speed,
                                        pitch = pitch
                                    )
                                )
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
                                    onHandlePlayerAction(
                                        PlayerActions.ApplyPlaybackSpeed(
                                            speed = speed,
                                            pitch = pitch
                                        )
                                    )
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
                                    .clip(RoundedCornerShape(11.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                    .clickable { showSpeedAndPitchChangerDialog = true }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            ) {
                                CuteText(
                                    text = "%.2f".format(speed),
                                    color = MaterialTheme.colorScheme.onBackground,
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
                            checked = shouldSnap,
                            onCheckedChange = { onChangeSnap() }
                        )
                        CuteText(
                            text = stringResource(id = R.string.snap)
                        )
                    }
                }
            }
        }
    )
}
