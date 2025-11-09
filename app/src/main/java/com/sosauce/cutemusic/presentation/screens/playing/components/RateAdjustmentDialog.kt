package com.sosauce.cutemusic.presentation.screens.playing.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.utils.rememberFocusRequester
import com.sosauce.cutemusic.utils.selfAlignHorizontally
import kotlinx.coroutines.android.awaitFrame

@Composable
fun RateAdjustmentDialog(
    rate: Float,
    onSetNewRate: (Float) -> Unit,
    onDismissRequest: () -> Unit,
    title: Int
) {
    val focusRequest = rememberFocusRequester()
    LaunchedEffect(Unit) {
        awaitFrame()
        focusRequest.requestFocus()
    }

    val textFieldState = rememberTextFieldState(initialText = "%.2f".format(rate))
    val newRate =
        remember(textFieldState.text) { textFieldState.text.toString().toFloatOrNull() ?: 1.0f }
    val isError = remember(newRate) { newRate > 2.0f || newRate < 0.5f }


    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(
                painter = painterResource(R.drawable.speed_rounded),
                contentDescription = null
            )
        },
        title = { Text(stringResource(id = title)) },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSetNewRate(newRate)
                    onDismissRequest()
                },
                enabled = !isError
            ) {
                Text(text = stringResource(id = R.string.set))
            }
        },
        text = {
            Column {
                Text(stringResource(id = R.string.new_rate))
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    state = textFieldState,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    onKeyboardAction = if (isError) null else KeyboardActionHandler {
                        onSetNewRate(
                            newRate
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    modifier = Modifier
                        .selfAlignHorizontally()
                        .fillMaxWidth(0.5f)
                        .focusRequester(focusRequest),
                    textStyle = TextStyle(
                        textAlign = TextAlign.Center
                    ),
                    isError = isError,
                    supportingText = {
                        if (isError) {
                            Text("Rate should be in range of 0.5 and 2.")
                        }
                    },
                    inputTransformation = RateInputTransformation
                )
            }
        }
    )
}

object RateInputTransformation : InputTransformation {
    override fun TextFieldBuffer.transformInput() {

        val input = asCharSequence()
        val dotCount = input.count { it == '.' }
        if (dotCount > 1) {
            revertAllChanges()
        } else {
            if (!input.all { it.isDigit() || it == '.' }) {
                revertAllChanges()
            }
        }
    }
}



