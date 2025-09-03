@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.utils.rememberInteractionSource

@Composable
fun SortingDropdownMenu(
    isSortedByASC: Boolean,
    onChangeSorting: (Boolean) -> Unit,
    sortingOptions: (@Composable () -> Unit)
) {

    val interactionSources = List(2) { rememberInteractionSource() }

    sortingOptions()
    ButtonGroup(
        overflowIndicator = {},
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .wrapContentWidth()
    ) {
        customItem(
            {
                ToggleButton(
                    checked = isSortedByASC,
                    onCheckedChange = { onChangeSorting(true) },
                    interactionSource = interactionSources[0],
                    modifier = Modifier.animateWidth(interactionSources[0])
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowUpward,
                        contentDescription = null
                    )
                }
            },
            {}
        )
        customItem(
            {
                ToggleButton(
                    checked = !isSortedByASC,
                    onCheckedChange = { onChangeSorting(false) },
                    interactionSource = interactionSources[1],
                    modifier = Modifier.animateWidth(interactionSources[1])
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowDownward,
                        contentDescription = null
                    )
                }
            },
            {}
        )
    }
}