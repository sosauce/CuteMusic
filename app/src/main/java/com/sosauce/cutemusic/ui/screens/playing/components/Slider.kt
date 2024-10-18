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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.data.MusicState
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberUseClassicSlider
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import me.saket.squiggles.SquigglySlider
import java.util.Locale

@Composable
fun MusicSlider(
    viewModel: MusicViewModel,
    musicState: MusicState
) {

    val sliderPosition = rememberUpdatedState(musicState.currentPosition)
    val useClassicSlider by rememberUseClassicSlider()
    val interactionSource = remember { MutableInteractionSource() }

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
                CuteText(
                    text = totalDuration(musicState.currentMusicDuration),
                )
            }
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(11.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                CuteText(
                    text = timeLeft(musicState.currentPosition)
                )
            }
        }
        if (useClassicSlider) {
            Slider(
                value = sliderPosition.value.toFloat(),
                onValueChange = {
                    musicState.currentPosition = it.toLong()
                    viewModel.handlePlayerActions(PlayerActions.SeekToSlider(musicState.currentPosition))
                },
                valueRange = 0f..musicState.currentMusicDuration.toFloat(),
                onValueChangeFinished = {
                    viewModel.handlePlayerActions(PlayerActions.SeekToSlider(musicState.currentPosition))
                },
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
                value = sliderPosition.value.toFloat(),
                onValueChange = {
                    musicState.currentPosition = it.toLong()
                    viewModel.handlePlayerActions(PlayerActions.SeekToSlider(musicState.currentPosition))
                },
                valueRange = 0f..musicState.currentMusicDuration.toFloat(),
                onValueChangeFinished = {
                    viewModel.handlePlayerActions(PlayerActions.SeekToSlider(musicState.currentPosition))
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


fun totalDuration(
    currentMusicDuration: Long
): String {
    val totalSeconds = currentMusicDuration / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}

fun timeLeft(
    currentPosition: Long
): String {
    val totalSeconds = currentPosition / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}
