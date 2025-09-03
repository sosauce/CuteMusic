@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.presentation.screens.playing.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.presentation.shared_components.AnimatedSlider
import com.sosauce.cutemusic.presentation.shared_components.CuteText
import com.sosauce.cutemusic.presentation.shared_components.RoundedCheckbox


@Composable
fun SpeedCard(
    musicState: MusicState,
    onDismissRequest: () -> Unit,
    onHandlePlayerAction: (PlayerActions) -> Unit,
    shouldSnap: Boolean,
    onChangeSnap: () -> Unit
) {
    var speedCardContent by remember { mutableStateOf(SpeedCardContent.DEFAULT) }
    val speed by animateFloatAsState(musicState.speed)
    val pitch by animateFloatAsState(musicState.pitch)
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
        icon = {
            Icon(
                painter = painterResource(R.drawable.speed_rounded),
                contentDescription = null
            )
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
                                            onClick = {
                                                onHandlePlayerAction(
                                                    PlayerActions.SetSpeed(
                                                        1.0f
                                                    )
                                                )
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
                                    AnimatedSlider(
                                        value = speed,
                                        onValueChange = {
                                            onHandlePlayerAction(
                                                PlayerActions.SetSpeed(
                                                    it
                                                )
                                            )
                                        },
                                        valueRange = 0.5f..2f
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
                                            onClick = {
                                                onHandlePlayerAction(
                                                    PlayerActions.SetPitch(
                                                        1.0f
                                                    )
                                                )
                                            },
                                            enabled = pitch != 1.0f
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.reset),
                                                contentDescription = null,
                                                tint = pitchTint
                                            )
                                        }
                                    }
                                    AnimatedSlider(
                                        value = pitch,
                                        onValueChange = {
                                            onHandlePlayerAction(
                                                PlayerActions.SetPitch(
                                                    it
                                                )
                                            )
                                        },
                                        valueRange = 0.5f..2f
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
                                                    onHandlePlayerAction(PlayerActions.SetSpeed(1.0f))
                                                    onHandlePlayerAction(PlayerActions.SetPitch(1.0f))
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
                                        AnimatedSlider(
                                            value = speed,
                                            onValueChange = {
                                                onHandlePlayerAction(PlayerActions.SetSpeed(it))
                                                onHandlePlayerAction(PlayerActions.SetPitch(it))
                                            },
                                            valueRange = 0.5f..2f
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    RoundedCheckbox(
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
                                onHandlePlayerAction(PlayerActions.SetSpeed(rate))
                                onHandlePlayerAction(PlayerActions.SetPitch(rate))
                                speedCardContent = SpeedCardContent.DEFAULT
                            },
                        )
                    }

                    SpeedCardContent.SPEED -> {
                        RateAdjustmentDialog(
                            rate = speed,
                            onSetNewRate = { rate ->
                                onHandlePlayerAction(PlayerActions.SetSpeed(rate))
                                speedCardContent = SpeedCardContent.DEFAULT
                            },
                        )
                    }

                    SpeedCardContent.PITCH -> {
                        RateAdjustmentDialog(
                            rate = pitch,
                            onSetNewRate = { rate ->
                                onHandlePlayerAction(PlayerActions.SetPitch(rate))
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
