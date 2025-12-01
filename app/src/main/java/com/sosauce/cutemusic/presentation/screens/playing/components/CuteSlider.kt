@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.playing.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sosauce.cutemusic.data.datastore.rememberSliderStyle
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.utils.formatToReadableTime

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
        valueRange = 0f..musicState.duration.toFloat(),
        enabled = true
    )


    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = musicState.position.formatToReadableTime(),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = musicState.duration.formatToReadableTime(),
                color = MaterialTheme.colorScheme.primary
            )
        }
        sliderStyle.toSlider(
            state = sliderState,
            isPlaying = musicState.isPlaying
        )

    }
}