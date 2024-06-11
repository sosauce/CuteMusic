@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.ui.screens.playing.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import com.sosauce.cutemusic.ui.theme.GlobalFont
import kotlinx.coroutines.delay
import me.saket.squiggles.SquigglySlider
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

@SuppressLint("SuspiciousIndentation")
@Composable
fun MusicSlider(viewModel: MusicViewModel) {
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    val updatedSliderPosition = rememberUpdatedState(sliderPosition)

    LaunchedEffect(Unit) {
        while (true) {
            sliderPosition = viewModel.currentPosition.toFloat()
            delay(1.seconds)
        }
    }
    Column {
        if (viewModel.currentMusicDuration > 0) {
            SquigglySlider(
                value = viewModel.currentPosition.toFloat(),
                onValueChange = { new ->
                    viewModel.currentPosition = new.toLong()
                    viewModel.handlePlayerActions(PlayerActions.SeekTo(new.toLong()))
                },
                valueRange = 0f..viewModel.currentMusicDuration.toFloat()
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = totalDuration(viewModel),
                        fontFamily = GlobalFont
                    )
                }
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = timeLeft(viewModel),
                        fontFamily = GlobalFont,
                    )
                }
            }

        } else {
            SquigglySlider(
                value = updatedSliderPosition.value,
                onValueChange = {},
                valueRange = 0f..0f

            )
        }
    }
}

fun totalDuration(viewModel: MusicViewModel): String {
    val totalSeconds = viewModel.currentMusicDuration / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}

fun timeLeft(viewModel: MusicViewModel): String {
    val totalSeconds = viewModel.currentPosition / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}
