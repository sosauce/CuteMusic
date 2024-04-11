package com.sosauce.cutemusic.components

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import com.sosauce.cutemusic.ui.theme.GlobalFont

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun DeleteDialog(
    openDialog: MutableState<Boolean>,
    musicUri: Uri
) {
    val context = LocalContext.current
    val contentResolver = LocalContext.current.contentResolver





    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = {
                    Text(
                        text = "Delete Music ?",
                        fontFamily = GlobalFont
                    )
            },
            text = {
                Text(
                    text = "Are you sure you want to permanently delete this music ?",
                    fontFamily = GlobalFont
                )
            },
            confirmButton = { 
                 TextButton(
                    onClick = { TODO() }
                ) {
                    Text(
                        text = "Delete",
                        fontFamily = GlobalFont,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                 IconButton(
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text(
                        text = "No",
                        fontFamily = GlobalFont,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
    }
}