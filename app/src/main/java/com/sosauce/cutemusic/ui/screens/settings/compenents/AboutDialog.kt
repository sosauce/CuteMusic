@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.ui.screens.settings.compenents

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.ui.shared_components.CuteText

@Composable
fun AboutDialog(
    title: String,
    contentText: String,
    onDismiss: () -> Unit
) {

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                CuteText(
                    text = stringResource(id = R.string.okay),

                    )
            }
        },
        title = {
            CuteText(
                text = title,

                )
        },
        text = {
            CuteText(
                text = contentText,

                )
        },
    )

}