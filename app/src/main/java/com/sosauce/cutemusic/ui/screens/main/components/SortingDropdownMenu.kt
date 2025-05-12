@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.ui.screens.main.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberAlbumGrids
import com.sosauce.cutemusic.data.datastore.rememberGroupByFolders
import com.sosauce.cutemusic.ui.shared_components.CuteDropdownMenuItem
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
        CuteDropdownMenuItem(
            onClick = { groupByFolders = !groupByFolders },
            text = { CuteText(stringResource(R.string.group_tracks)) },
            leadingIcon = {
                Checkbox(
                    checked = groupByFolders,
                    onCheckedChange = null
                )
            }
        )
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

@Composable
fun AlbumSortingDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    isSortedByASC: Boolean,
    onChangeSorting: (Boolean) -> Unit,
    onGridSelectionExpanded: () -> Unit
) {
    var numberOfAlbumGrids by rememberAlbumGrids()


    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(24.dp)
    ) {
        CuteDropdownMenuItem(
            onClick = onGridSelectionExpanded,
            text = { CuteText(stringResource(R.string.no_of_grids)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.grid_view),
                    contentDescription = null
                )
            },
            trailingIcon = { CuteText(numberOfAlbumGrids.toString()) }
        )
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