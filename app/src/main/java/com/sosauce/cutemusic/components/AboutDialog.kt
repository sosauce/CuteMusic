@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.sosauce.cutemusic.ui.theme.GlobalFont

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
                Text(
                    text = "Okay",
                    fontFamily = GlobalFont
                )}
        },
        title = {
            Text(
                text = title,
                fontFamily = GlobalFont
            )
        },
        text = {
            Text(
                text = contentText,
                fontFamily = GlobalFont
            )
        },
    )

}