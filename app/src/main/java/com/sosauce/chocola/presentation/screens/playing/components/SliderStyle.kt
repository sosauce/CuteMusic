@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.playing.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.data.datastore.rememberThumblessSlider
import com.sosauce.chocola.presentation.shared_components.animations.AnimatedSlider
import com.sosauce.chocola.utils.SliderStyle
import com.sosauce.chocola.utils.rememberInteractionSource


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
            Slider(
                value = state.value,
                onValueChange = state.onValueChange,
                onValueChangeFinished = state.onValueChangeFinished,
                valueRange = state.valueRange,
                enabled = state.enabled,
                interactionSource = interactionSource,
                track = { sliderState2 ->

                    val animatedHeight by animateDpAsState(
                        if (sliderState2.isDragging) 7.dp else 4.dp
                    )
                    val trackStroke = Stroke(
                        width =
                            with(LocalDensity.current) {
                                animatedHeight.toPx()
                            },
                        cap = StrokeCap.Round,
                    )

                    LinearWavyProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        progress = {
                            if (state.valueRange.endInclusive > 0) {
                                sliderState2.value / state.valueRange.endInclusive
                            } else 0f
                        },
                        stopSize = 0.dp,
                        trackStroke = trackStroke,
                        amplitude = { if (isPlaying && !sliderState2.isDragging) 1f else 0f }
                    )
                },
                thumb = {
                    if (!hideThumb) {
                        val animatedHeight by animateDpAsState(
                            if (it.isDragging) 40.dp else 35.dp
                        )

                        val animatedWidth by animateDpAsState(
                            if (it.isDragging) 10.dp else 6.dp
                        )

                        SliderDefaults.Thumb(
                            interactionSource = remember { MutableInteractionSource() },
                            thumbSize = DpSize(animatedWidth, animatedHeight)
                        )
                    }
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