package com.sosauce.cutemusic.ui.screens.main.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberGroupByFolders
import com.sosauce.cutemusic.ui.shared_components.CuteText

@Composable
fun SortingDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    isSortedByASC: Boolean,
    onChangeSorting: (Boolean) -> Unit
) {
    var groupByFolders by rememberGroupByFolders()
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(24.dp),
    ) {
        DropdownMenuItem(
            onClick = { groupByFolders = !groupByFolders },
            text = { CuteText(stringResource(R.string.group_tracks)) },
            leadingIcon = {
                Checkbox(
                    checked = groupByFolders,
                    onCheckedChange = null
                )
            },
            modifier = Modifier
                .height(56.dp)
                .padding(5.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        DropdownMenuItem(
            onClick = { onChangeSorting(true) },
            text = { CuteText(stringResource(R.string.ascending)) },
            leadingIcon = {
                RadioButton(
                    selected = isSortedByASC,
                    onClick = null
                )
            },
            modifier = Modifier
                .height(56.dp)
                .padding(5.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        DropdownMenuItem(
            onClick = { onChangeSorting(false) },
            text = { CuteText(stringResource(R.string.descending)) },
            leadingIcon = {
                RadioButton(
                    selected = !isSortedByASC,
                    onClick = null
                )
            },
            modifier = Modifier
                .height(56.dp)
                .padding(5.dp)
                .clip(RoundedCornerShape(12.dp))
        )
    }
}