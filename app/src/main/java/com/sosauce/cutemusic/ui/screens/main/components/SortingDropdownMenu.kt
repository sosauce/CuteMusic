@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.ui.screens.main.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.ui.shared_components.CuteDropdownMenuItem
import com.sosauce.cutemusic.ui.shared_components.CuteText

@Composable
fun SortingDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    isSortedByASC: Boolean,
    onChangeSorting: (Boolean) -> Unit,
    additionalActions: (@Composable () -> Unit)? = null
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(24.dp),
    ) {
        additionalActions?.invoke()
        CuteDropdownMenuItem(
            onClick = { onChangeSorting(true) },
            text = { CuteText(stringResource(R.string.ascending)) },
            leadingIcon = {
                RadioButton(
                    selected = isSortedByASC,
                    onClick = null
                )
            }
        )
        CuteDropdownMenuItem(
            onClick = { onChangeSorting(false) },
            text = { CuteText(stringResource(R.string.descending)) },
            leadingIcon = {
                RadioButton(
                    selected = !isSortedByASC,
                    onClick = null
                )
            }
        )
    }
}