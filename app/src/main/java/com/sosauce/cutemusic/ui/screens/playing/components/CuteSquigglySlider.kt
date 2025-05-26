@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.ui.screens.playing.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import me.saket.squiggles.SquigglySlider
import me.saket.squiggles.SquigglySlider.SquigglesAnimator
import me.saket.squiggles.SquigglySlider.SquigglesSpec

/**
 * Squiggly slider but you can pass a thumb
 */
@Composable
fun CuteSquigglySlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColors = SliderDefaults.colors(),
    squigglesSpec: SquigglesSpec = SquigglesSpec(),
    squigglesAnimator: SquigglesAnimator = SquigglySlider.rememberSquigglesAnimator(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    thumb: @Composable (SliderState) -> Unit = {
        SquigglySlider.Thumb(
            interactionSource = interactionSource,
            colors = colors,
            enabled = enabled,
            thumbSize = DpSize(
                width = squigglesSpec.strokeWidth.coerceAtLeast(4.dp),
                height = (squigglesSpec.strokeWidth * 4).coerceAtLeast(16.dp),
            )
        )
    },
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        onValueChangeFinished = onValueChangeFinished,
        colors = colors,
        interactionSource = interactionSource,
        thumb = thumb,
        track = { sliderState ->
            SquigglySlider.Track(
                interactionSource = interactionSource,
                colors = colors,
                enabled = enabled,
                sliderState = sliderState,
                squigglesSpec = squigglesSpec,
                squigglesAnimator = squigglesAnimator,
            )
        },
        valueRange = valueRange
    )
}