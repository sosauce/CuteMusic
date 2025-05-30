package com.sosauce.cutemusic.ui.shared_components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.IntOffset
import com.sosauce.cutemusic.utils.rememberAnimatable
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

    IconButton(
        onClick = {
            onClick()
            scope.launch {
                animatable.animateTo(
                    targetValue = animationDirection,
                    animationSpec = tween(500)
                )
                animatable.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(500)
                )
            }
        },
        modifier = buttonModifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = modifier
                .offset {
                    IntOffset(
                        x = animatable.value.toInt(),
                        y = 0
                    )
                }
        )
    }
}