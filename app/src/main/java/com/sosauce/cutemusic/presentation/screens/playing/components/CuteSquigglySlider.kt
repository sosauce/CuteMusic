@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.playing.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
    thumb: @Composable (SliderState) -> Unit,
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
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
        valueRange = valueRange,
    )
}