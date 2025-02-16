package com.sosauce.cutemusic.ui.screens.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
        modifier = Modifier.background(color = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(15.dp)
            ) {
                Checkbox(
                    modifier = Modifier.size(20.dp), // https://stackoverflow.com/a/77142600/28577483
                    checked = groupByFolders,
                    onCheckedChange = { groupByFolders = it }
                )
                Spacer(Modifier.width(10.dp))
                CuteText(stringResource(R.string.group_tracks))
            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth()
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onChangeSorting(true) }
                    .padding(10.dp)
            ) {
                RadioButton(
                    selected = isSortedByASC,
                    onClick = null,

                    )
                Spacer(Modifier.width(10.dp))
                CuteText(stringResource(R.string.ascending))
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onChangeSorting(false) }
                    .padding(10.dp)
            ) {
                RadioButton(
                    selected = !isSortedByASC,
                    onClick = null
                )
                Spacer(Modifier.width(10.dp))
                CuteText(stringResource(R.string.descending))
            }

        }
    }
}