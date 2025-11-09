package com.sosauce.cutemusic.presentation.shared_components.animations

import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R

@Composable
fun AnimatedPlayPauseIcon(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    size: Dp = 24.dp
) {
    val playToPause = rememberAnimatedVectorPainter(
        animatedImageVector = AnimatedImageVector.animatedVectorResource(R.drawable.pause_to_play),
        atEnd = !isPlaying
    )

    Icon(
        painter = playToPause,
        contentDescription = null,
        modifier = modifier.size(size)
    )
}