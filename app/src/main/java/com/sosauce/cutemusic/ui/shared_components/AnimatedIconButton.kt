package com.sosauce.cutemusic.ui.shared_components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.IntOffset
import com.sosauce.cutemusic.utils.rememberAnimatable
import com.sosauce.cutemusic.utils.rememberInteractionSource
import kotlinx.coroutines.launch

@Composable
fun AnimatedIconButton(
    modifier: Modifier = Modifier,
    buttonModifier: Modifier = Modifier,
    onClick: () -> Unit,
    animationDirection: Float,
    icon: ImageVector,
    contentDescription: String
) {
    val scope = rememberCoroutineScope()
    val animatable = rememberAnimatable()
    val interactionSource = rememberInteractionSource()
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.7f else 1f
    )


    IconButton(
        onClick = {
            onClick()
            scope.launch {
                animatable.animateTo(animationDirection)
                animatable.animateTo(0f)
            }
        },
        modifier = buttonModifier,
        interactionSource = interactionSource
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = modifier
                .offset {
                    IntOffset(
                        x = animatable.value.toInt(),
                        y = 0
                    )
                }
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        )
    }
}