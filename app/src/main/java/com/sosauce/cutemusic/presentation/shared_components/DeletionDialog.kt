package com.sosauce.cutemusic.presentation.shared_components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sosauce.cutemusic.R

/**
 * A dialog that should be used as a confirmation to delete
 */
@Composable
fun DeletionDialog(
    onDismissRequest: () -> Unit,
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.are_u_sure)) },
        confirmButton = {
            TextButton(
                onClick = {
                    onDelete()
                    onDismissRequest()
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.trash_rounded),
                contentDescription = null
            )
        },
        text = content
    )

}