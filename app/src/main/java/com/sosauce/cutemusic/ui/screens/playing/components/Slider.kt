@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.ui.screens.playing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberUseClassicSlider
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.utils.formatToReadableTime
import me.saket.squiggles.SquigglySlider

@Composable
fun MusicSlider(
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {

    val useClassicSlider by rememberUseClassicSlider()
    val interactionSource = remember { MutableInteractionSource() }
    var tempSliderValue by remember { mutableStateOf<Float?>(null) }


    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(11.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                CuteText(musicState.currentPosition.formatToReadableTime())
            }
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(11.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                CuteText(musicState.currentMusicDuration.formatToReadableTime())
            }
        }
        if (useClassicSlider) {
            Slider(
                value = tempSliderValue ?: musicState.currentPosition.toFloat(),
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
                valueRange = 0f..musicState.currentMusicDuration.toFloat(),
                modifier = Modifier.fillMaxWidth(),
                track = { sliderState ->
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        drawStopIndicator = null,
                        thumbTrackGapSize = 0.dp,
                        modifier = Modifier.height(4.dp)
                    )
                },
                thumb = {
                    SliderDefaults.Thumb(
                        interactionSource = interactionSource,
                        thumbSize = DpSize(width = 20.dp, height = 20.dp)
                    )
                }
            )
        } else {
            SquigglySlider(
                value = tempSliderValue ?: musicState.currentPosition.toFloat(),
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
                valueRange = 0f..musicState.currentMusicDuration.toFloat(),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
