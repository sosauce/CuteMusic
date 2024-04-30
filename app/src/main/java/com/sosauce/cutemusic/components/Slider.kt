@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.components

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
import androidx.media3.common.Player
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.ui.theme.GlobalFont
import kotlinx.coroutines.delay
import me.saket.squiggles.SquigglySlider
import kotlin.time.Duration.Companion.seconds

@SuppressLint("SuspiciousIndentation")
@Composable
fun MusicSlider(
    viewModel: MusicViewModel,
    player: Player
) {

    var sliderPosition by remember { mutableFloatStateOf(viewModel.currentValue.toFloat()) }
    val updatedSliderPosition = rememberUpdatedState(sliderPosition)

        LaunchedEffect(Unit) {
            while (true) {
                val currentPosition = player.currentPosition
                viewModel.currentValue = currentPosition
                sliderPosition = currentPosition.toFloat()
                delay(1.seconds / 30 )
            }
        }
    Column {
        if (player.duration > 0) {
            SquigglySlider(
                value = updatedSliderPosition.value,
                onValueChange = { new ->
                    sliderPosition = new
                    viewModel.currentValue = new.toLong()
                    player.seekTo(viewModel.currentValue)
                },
                valueRange = 0f..player.duration.toFloat()
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
                            shape = RoundedCornerShape(16.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (viewModel.totalDuration() == "-153722867280912:-55") "" else viewModel.totalDuration(),   //lame check need to change
                        fontFamily = GlobalFont
                    )
                }
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (viewModel.timeLeft() == "-153722867280912:-55") "" else viewModel.timeLeft(),   //lame check need to change
                        fontFamily = GlobalFont,
                    )
                }
            }

        } else {
            SquigglySlider(
                value = updatedSliderPosition.value,
                onValueChange = { new ->
                    sliderPosition = new
                    viewModel.currentValue = new.toLong()
                    player.seekTo(viewModel.currentValue)
                },
                valueRange = 0f..0f

            )
        }
    }
}
