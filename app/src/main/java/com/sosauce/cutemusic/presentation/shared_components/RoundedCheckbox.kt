package com.sosauce.cutemusic.presentation.shared_components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.state.ToggleableState
import kotlin.math.floor

@Composable
fun RoundedCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    val strokeWidthPx = with(LocalDensity.current) { floor(CheckboxDefaults.StrokeWidth.toPx()) }
    TriStateCheckbox(
        state = ToggleableState(checked),
        onClick =
            if (onCheckedChange != null) {
                { onCheckedChange(!checked) }
            } else {
                null
            },
        checkmarkStroke = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
        outlineStroke = Stroke(width = strokeWidthPx),
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
    )
}