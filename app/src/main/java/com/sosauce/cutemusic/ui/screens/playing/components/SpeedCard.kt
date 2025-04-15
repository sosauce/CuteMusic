@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.ui.screens.playing.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberPitch
import com.sosauce.cutemusic.data.datastore.rememberSpeed
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.utils.rememberInteractionSource


@Composable
fun SpeedCard(
    onDismissRequest: () -> Unit,
    shouldSnap: Boolean,
    onChangeSnap: () -> Unit
) {
    var speed by rememberSpeed()
    var pitch by rememberPitch()
    var speedCardContent by remember { mutableStateOf(SpeedCardContent.DEFAULT) }
    val interactionSource = rememberInteractionSource()
    val speedValue by animateFloatAsState(speed)
    val pitchValue by animateFloatAsState(pitch)
    val speedTint by animateColorAsState(
        targetValue = if (speed != 1.0f) LocalContentColor.current else Color.Transparent,
        animationSpec = tween(500)
    )
    val pitchTint by animateColorAsState(
        targetValue = if (pitch != 1.0f) LocalContentColor.current else Color.Transparent,
        animationSpec = tween(500)
    )

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(onClick = { onDismissRequest() }) {
                CuteText(text = stringResource(id = R.string.okay))
            }
        },
        title = {
            AnimatedContent(
                targetState = speedCardContent
            ) {
                when (it) {
                    SpeedCardContent.DEFAULT -> {
                        CuteText(stringResource(id = R.string.playback_speed))
                    }

                    SpeedCardContent.RATE -> {
                        CuteText(stringResource(id = R.string.set_sap))
                    }

                    SpeedCardContent.SPEED -> {
                        CuteText(stringResource(id = R.string.set_speed))
                    }

                    SpeedCardContent.PITCH -> {
                        CuteText(stringResource(id = R.string.set_pitch))
                    }
                }
            }
        },
        text = {
            AnimatedContent(
                targetState = speedCardContent
            ) {
                when (it) {
                    SpeedCardContent.DEFAULT -> {
                        Box {
                            Column {
                                if (!shouldSnap) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(11.dp))
                                                .background(
                                                    MaterialTheme.colorScheme.primary.copy(
                                                        alpha = 0.1f
                                                    )
                                                )
                                                .clickable {
                                                    speedCardContent = SpeedCardContent.SPEED
                                                }
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
                                        IconButton(
                                            onClick = { speed = 1.0f },
                                            enabled = speed != 1.0f
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.reset),
                                                contentDescription = null,
                                                tint = speedTint
                                            )
                                        }
                                    }
                                    Slider(
                                        value = speedValue,
                                        onValueChange = { speed = it },
                                        valueRange = 0.5f..2f,
                                        track = { sliderState ->
                                            SliderDefaults.Track(
                                                sliderState = sliderState,
                                                drawStopIndicator = null,
                                                thumbTrackGapSize = 4.dp,
                                                modifier = Modifier.height(5.dp),
                                                trackInsideCornerSize = 3.dp
                                            )
                                        },
                                        thumb = {
                                            SliderDefaults.Thumb(
                                                interactionSource = interactionSource,
                                                thumbSize = DpSize(width = 4.dp, height = 30.dp)
                                            )
                                        },
                                        interactionSource = interactionSource
                                    )
                                    //
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(11.dp))
                                                .background(
                                                    MaterialTheme.colorScheme.primary.copy(
                                                        alpha = 0.1f
                                                    )
                                                )
                                                .clickable {
                                                    speedCardContent = SpeedCardContent.PITCH
                                                }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            CuteText(
                                                text = "${stringResource(id = R.string.pitch)}: " + "%.2f".format(
                                                    pitch
                                                ),
                                                color = MaterialTheme.colorScheme.onBackground,
                                                fontSize = 15.sp
                                            )
                                        }
                                        IconButton(
                                            onClick = { pitch = 1.0f },
                                            enabled = pitch != 1.0f
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.reset),
                                                contentDescription = null,
                                                tint = pitchTint
                                            )
                                        }
                                    }
                                    Slider(
                                        value = pitchValue,
                                        onValueChange = { pitch = it },
                                        valueRange = 0.5f..2f,
                                        track = { sliderState ->
                                            SliderDefaults.Track(
                                                sliderState = sliderState,
                                                drawStopIndicator = null,
                                                thumbTrackGapSize = 4.dp,
                                                modifier = Modifier.height(5.dp),
                                                trackInsideCornerSize = 3.dp
                                            )
                                        },
                                        thumb = {
                                            SliderDefaults.Thumb(
                                                interactionSource = interactionSource,
                                                thumbSize = DpSize(width = 4.dp, height = 30.dp)
                                            )
                                        },
                                        interactionSource = interactionSource
                                    )
                                } else {
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(11.dp))
                                                    .background(
                                                        MaterialTheme.colorScheme.primary.copy(
                                                            alpha = 0.1f
                                                        )
                                                    )
                                                    .clickable {
                                                        speedCardContent = SpeedCardContent.RATE
                                                    }
                                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                            ) {
                                                CuteText(
                                                    text = "${stringResource(id = R.string.rate)}: " + "%.2f".format(
                                                        speed
                                                    ),
                                                    color = MaterialTheme.colorScheme.onBackground,
                                                )
                                            }
                                            IconButton(
                                                onClick = {
                                                    speed = 1.0f
                                                    pitch = 1.0f
                                                },
                                                enabled = speed != 1.0f
                                            ) {
                                                Icon(
                                                    painter = painterResource(R.drawable.reset),
                                                    contentDescription = null,
                                                    tint = speedTint
                                                )
                                            }
                                        }
                                        Slider(
                                            value = speedValue,
                                            onValueChange = {
                                                speed = it
                                                pitch = it
                                            },
                                            valueRange = 0.5f..2f,
                                            track = { sliderState ->
                                                SliderDefaults.Track(
                                                    sliderState = sliderState,
                                                    drawStopIndicator = null,
                                                    thumbTrackGapSize = 4.dp,
                                                    modifier = Modifier.height(5.dp),
                                                    trackInsideCornerSize = 3.dp
                                                )
                                            },
                                            thumb = {
                                                SliderDefaults.Thumb(
                                                    interactionSource = interactionSource,
                                                    thumbSize = DpSize(width = 4.dp, height = 30.dp)
                                                )
                                            },
                                            interactionSource = interactionSource
                                        )
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

                    SpeedCardContent.RATE -> {
                        RateAdjustmentDialog(
                            rate = speed,
                            onSetNewRate = { rate ->
                                speed = rate
                                pitch = rate
                                speedCardContent = SpeedCardContent.DEFAULT
                            },
                        )
                    }

                    SpeedCardContent.SPEED -> {
                        RateAdjustmentDialog(
                            rate = speed,
                            onSetNewRate = { rate ->
                                speed = rate
                                speedCardContent = SpeedCardContent.DEFAULT
                            },
                        )
                    }

                    SpeedCardContent.PITCH -> {
                        RateAdjustmentDialog(
                            rate = pitch,
                            onSetNewRate = { rate ->
                                pitch = rate
                                speedCardContent = SpeedCardContent.DEFAULT
                            },
                        )
                    }
                }
            }
        }
    )
}

enum class SpeedCardContent {
    DEFAULT,
    RATE,
    SPEED,
    PITCH
}
