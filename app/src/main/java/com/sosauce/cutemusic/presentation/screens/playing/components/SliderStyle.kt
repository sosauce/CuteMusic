@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.playing.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.data.datastore.rememberThumblessSlider
import com.sosauce.cutemusic.presentation.shared_components.AnimatedSlider
import com.sosauce.cutemusic.utils.SliderStyle
import com.sosauce.cutemusic.utils.rememberInteractionSource
import me.saket.squiggles.SquigglySlider


@Composable
fun String.toSlider(
    state: CuteSliderState,
    isPlaying: Boolean = true
) {
    val interactionSource = rememberInteractionSource()
    val isDragging by interactionSource.collectIsDraggedAsState()
    val hideThumb by rememberThumblessSlider()
    when (this) {
        SliderStyle.WAVY -> {

            val width by animateDpAsState(
                targetValue = if (isDragging) 6.dp else 4.dp
            )
            val amplitude by animateDpAsState(
                targetValue = if (isPlaying) 5.dp else 0.dp
            )
            val squigglesSpec = remember(width, amplitude) {
                SquigglySlider.SquigglesSpec(
                    wavelength = 40.dp,
                    amplitude = amplitude,
                    strokeWidth = width
                )
            }

            CuteSquigglySlider(
                value = state.value,
                onValueChange = state.onValueChange,
                onValueChangeFinished = state.onValueChangeFinished,
                valueRange = state.valueRange,
                enabled = state.enabled,
                interactionSource = interactionSource,
                squigglesSpec = squigglesSpec,
                colors = SliderDefaults.colors(
                    disabledActiveTrackColor = MaterialTheme.colorScheme.primary,
                    disabledThumbColor = MaterialTheme.colorScheme.primary,
                ),
                thumb = {
                    val height by animateDpAsState(
                        targetValue = if (hideThumb) 0.dp else (squigglesSpec.strokeWidth * 6).coerceAtLeast(
                            16.dp
                        ),
                    )

                    SquigglySlider.Thumb(
                        interactionSource = interactionSource,
                        colors = SliderDefaults.colors(
                            disabledThumbColor = MaterialTheme.colorScheme.primary
                        ),
                        enabled = state.enabled,
                        thumbSize = DpSize(
                            width = squigglesSpec.strokeWidth.coerceAtLeast(4.dp),
                            height = height
                        )
                    )
                }
            )
        }

        SliderStyle.CLASSIC -> {
            Slider(
                value = state.value,
                onValueChange = state.onValueChange,
                onValueChangeFinished = state.onValueChangeFinished,
                track = { sliderState ->
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        drawStopIndicator = null,
                        thumbTrackGapSize = 0.dp,
                        modifier = Modifier.height(4.dp)
                    )
                },
                thumb = {
                    val width by animateDpAsState(
                        targetValue = if (isDragging) 28.dp else 20.dp
                    )
                    val height by animateDpAsState(
                        targetValue = if (hideThumb) 0.dp else 20.dp
                    )
                    SliderDefaults.Thumb(
                        interactionSource = rememberInteractionSource(),
                        thumbSize = DpSize(
                            width = width,
                            height = height
                        )
                    )
                },
                valueRange = state.valueRange,
                enabled = state.enabled,
                interactionSource = interactionSource
            )
        }

        SliderStyle.MATERIAL3 -> {
            AnimatedSlider(
                value = state.value,
                onValueChange = state.onValueChange,
                onValueChangeFinished = state.onValueChangeFinished,
                hideThumb = hideThumb,
                valueRange = state.valueRange,
                enabled = state.enabled,
                interactionSource = interactionSource
            )
        }

        else -> Unit
    }
}


data class CuteSliderState(
    val value: Float,
    val onValueChange: (Float) -> Unit,
    val onValueChangeFinished: (() -> Unit)?,
    val valueRange: ClosedFloatingPointRange<Float>,
    val enabled: Boolean
)

@Composable
fun rememberCuteSliderState(
    value: Float = 1f,
    onValueChange: (Float) -> Unit = {},
    onValueChangeFinished: (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    enabled: Boolean = true
): CuteSliderState {
    return remember(value) {
        CuteSliderState(
            value = value,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = valueRange,
            enabled = enabled
        )
    }
}