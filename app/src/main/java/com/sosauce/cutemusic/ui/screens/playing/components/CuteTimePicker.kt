@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.ui.screens.playing.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.ui.shared_components.CuteText

@Composable
fun CuteTimePicker(
    initialMillis: Long = 0,
    onDismissRequest: () -> Unit,
    onSetTimer: (hours: Int, minutes: Int) -> Unit
) {


    val initialSeconds = remember { initialMillis / 1000 }
    val initialMinutes = remember { (initialSeconds / 60) % 60 }
    val initialHours = remember { (initialMinutes / 60) % 24 }

    // is24Hour is set to true to not show the AM/PM selector, as this time picker is used to start a countdown anyways
    val timePickerState = rememberTimePickerState(
        is24Hour = true,
        initialMinute = initialMinutes.toInt(),
        initialHour = initialHours.toInt(),
    )

    TimePickerDialog(
        title = {
            CuteText(
                text = stringResource(R.string.set_sleep_timer),
                fontSize = 24.sp
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSetTimer(timePickerState.hour, timePickerState.minute) }
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
    ) {
        Spacer(Modifier.height(10.dp))
        TimePicker(timePickerState)
    }

//    AlertDialog(
//        text = { TimePicker(timePickerState) },
//        title = {
//            CuteText(stringResource(R.string.set_sleep_timer))
//        },
//        confirmButton = {
//            TextButton(
//                onClick = { onSetTimer(timePickerState.hour, timePickerState.minute) }
//            ) {
//                CuteText(stringResource(R.string.okay))
//            }
//        },
//        dismissButton = {
//            TextButton(
//                onClick = onDismissRequest
//            ) {
//                CuteText(stringResource(R.string.cancel))
//            }
//        },
//        onDismissRequest = onDismissRequest
//    )
}