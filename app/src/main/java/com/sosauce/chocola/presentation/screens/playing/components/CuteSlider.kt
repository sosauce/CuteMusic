@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.playing.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.data.datastore.rememberSliderStyle
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.utils.formatToReadableTime
import kotlin.math.abs

@Composable
fun CuteSlider(
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
) {
    val sliderStyle by rememberSliderStyle()
    var tempSliderValue by remember { mutableStateOf<Float?>(null) }
    val value by animateFloatAsState(
        targetValue = tempSliderValue ?: musicState.position.toFloat()
    )
    val sliderState = rememberCuteSliderState(
        value = value,
        onValueChange = { tempSliderValue = it },
        onValueChangeFinished = {
            tempSliderValue?.let {
                onHandlePlayerActions(
                    PlayerActions.UpdateCurrentPosition(it.toLong())
                )
                onHandlePlayerActions(
                    PlayerActions.SeekToSlider(it.toLong())
                )
            }
            tempSliderValue = null
        },
        valueRange = 0f..musicState.track.durationMs.toFloat(),
        enabled = true
    )


    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = musicState.position.formatToReadableTime(),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(5.dp))
            AnimatedVisibility(
                visible = tempSliderValue != null
            ) {

                val diff = (tempSliderValue?.toLong() ?: 0) - musicState.position

                val text = when {
                    diff > 0 -> "+ ${diff.formatToReadableTime()}"
                    diff < 0 -> "- ${abs(diff).formatToReadableTime()}"
                    else -> "00:00"
                }

                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMediumEmphasized.copy(
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = musicState.track.durationMs.formatToReadableTime(),
                color = MaterialTheme.colorScheme.primary
            )
        }
        sliderStyle.toSlider(
            state = sliderState,
            isPlaying = musicState.isPlaying
        )

    }
}