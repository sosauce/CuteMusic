@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.presentation.screens.playing.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.presentation.shared_components.SpeedAndPitchDialog


@Composable
fun SpeedCard(
    musicState: MusicState,
    onDismissRequest: () -> Unit,
    onHandlePlayerAction: (PlayerActions) -> Unit,
    shouldSnap: Boolean,
    onChangeSnap: () -> Unit
) {
    var speedCardContent by remember { mutableStateOf(SpeedCardContent.DEFAULT) }
    when (speedCardContent) {
        SpeedCardContent.DEFAULT -> {
            SpeedAndPitchDialog(
                musicState = musicState,
                shouldSnap = shouldSnap,
                onDismissRequest = onDismissRequest,
                onChangeSnap = onChangeSnap,
                onHandlePlayerAction = onHandlePlayerAction,
                onSetSpeedContent = { speedCardContent = it }
            )
        }

        SpeedCardContent.RATE -> {
            RateAdjustmentDialog(
                rate = musicState.speed,
                onSetNewRate = { rate ->
                    onHandlePlayerAction(PlayerActions.SetSpeed(rate))
                    onHandlePlayerAction(PlayerActions.SetPitch(rate))
                    onDismissRequest()
                },
                title = R.string.set_sap,
                onDismissRequest = onDismissRequest
            )
        }

        SpeedCardContent.SPEED -> {
            RateAdjustmentDialog(
                rate = musicState.speed,
                onSetNewRate = { speed ->
                    onHandlePlayerAction(PlayerActions.SetSpeed(speed))
                    onDismissRequest()
                },
                title = R.string.set_speed,
                onDismissRequest = onDismissRequest
            )
        }

        SpeedCardContent.PITCH -> {
            RateAdjustmentDialog(
                rate = musicState.pitch,
                onSetNewRate = { pitch ->
                    onHandlePlayerAction(PlayerActions.SetPitch(pitch))
                    onDismissRequest()
                },
                title = R.string.set_pitch,
                onDismissRequest = onDismissRequest
            )
        }

    }
}

enum class SpeedCardContent {
    DEFAULT,
    RATE,
    SPEED,
    PITCH
}
