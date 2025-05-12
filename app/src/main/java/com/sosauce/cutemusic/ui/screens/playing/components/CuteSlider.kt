@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.ui.screens.playing.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberUseClassicSlider
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.utils.formatToReadableTime
import com.sosauce.cutemusic.utils.rememberInteractionSource
import me.saket.squiggles.SquigglySlider

@Composable
fun CuteSlider(
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
) {
    val useClassicSlider by rememberUseClassicSlider()
    val interactionSource = rememberInteractionSource()
    val isDragging by interactionSource.collectIsDraggedAsState()
    var tempSliderValue by remember { mutableStateOf<Float?>(null) }
    val value by animateFloatAsState(
        targetValue = tempSliderValue ?: musicState.position.toFloat()
    )


    Column(
        modifier = Modifier.padding(horizontal = 15.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            CuteText(
                text = musicState.position.formatToReadableTime(),
                color = MaterialTheme.colorScheme.primary
            )
            CuteText(
                text = musicState.duration.formatToReadableTime(),
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
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
            track = { sliderState ->
                if (useClassicSlider) {
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        drawStopIndicator = null,
                        thumbTrackGapSize = 0.dp,
                        modifier = Modifier.height(4.dp)
                    )
                } else {
                    val amplitude by animateDpAsState(
                        targetValue = if (musicState.isPlaying && !isDragging) 5.dp else 0.dp,
                        animationSpec = MotionScheme.expressive().slowSpatialSpec()
                    )
                    SquigglySlider.Track(
                        interactionSource = rememberInteractionSource(),
                        colors = SliderDefaults.colors(),
                        enabled = true,
                        sliderState = sliderState,
                        squigglesSpec = SquigglySlider.SquigglesSpec(
                            amplitude = amplitude,
                            wavelength = 45.dp
                        )
                    )
                }
            },
            thumb = {
                val thumbWidth by animateDpAsState(
                    targetValue = when (useClassicSlider) {
                        true -> if (isDragging) 28.dp else 20.dp
                        false -> if (isDragging) 12.dp else 4.dp
                    }
                )
                SliderDefaults.Thumb(
                    interactionSource = rememberInteractionSource(),
                    thumbSize = DpSize(
                        width = thumbWidth,
                        height = if (useClassicSlider) 20.dp else 22.dp
                    ),
                )
            },
            valueRange = 0f..musicState.duration.toFloat(),
            interactionSource = interactionSource
        )
    }
}