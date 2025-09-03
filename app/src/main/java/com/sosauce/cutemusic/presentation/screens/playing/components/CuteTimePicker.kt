@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)

package com.sosauce.cutemusic.presentation.screens.playing.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.presentation.shared_components.CuteText
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@Composable
fun CuteTimePicker(
    initialMillis: Long,
    onDismissRequest: () -> Unit,
    onSetTimer: (hours: Long, minutes: Long) -> Unit
) {

    val initialHoursAndMins =
        initialMillis.toDuration(DurationUnit.MILLISECONDS).toComponents { hours, minutes, _, _ ->
            "$hours,$minutes"
        }

    val timePickerState = rememberTimePickerState(
        is24Hour = true,
        initialMinute = initialHoursAndMins.substringAfter(',').toInt(),
        initialHour = initialHoursAndMins.substringBefore(',').toInt(),
    )

    AlertDialog(
        title = {
            CuteText(
                text = stringResource(R.string.set_sleep_timer),
                fontSize = 24.sp
            )
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.sleep_timer_filled),
                contentDescription = null
            )
        },
        text = {
            TimePicker(timePickerState)
        },
        confirmButton = {

            TextButton(
                onClick = {
                    onSetTimer(
                        timePickerState.hour.toLong(),
                        timePickerState.minute.toLong()
                    )
                }
            ) {
                CuteText(stringResource(R.string.okay))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                CuteText(stringResource(R.string.cancel))
            }
        },
        onDismissRequest = onDismissRequest
    )
}