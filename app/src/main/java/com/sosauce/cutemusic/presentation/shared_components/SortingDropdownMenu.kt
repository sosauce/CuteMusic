@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.presentation.shared_components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MenuDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R

@Composable
fun SortingDropdownMenu(
    isSortedAscending: Boolean,
    onChangeSorting: (Boolean) -> Unit,
    topContent: @Composable (() -> Unit)? = null,
    content: @Composable (ColumnScope.() -> Unit)
) {

    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(
            onClick = { expanded = !expanded },
            shapes = IconButtonDefaults.shapes()
        ) {
            AnimatedContent(
                targetState = expanded
            ) { isExpanded ->
                val icon = if (!isExpanded) R.drawable.sort else R.drawable.close
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null
                )
            }
        }
        DropdownMenuPopup(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuGroup(
                shapes = MenuDefaults.groupShape(0, 2)
            ) { topContent?.invoke() }
            Spacer(Modifier.height(MenuDefaults.GroupSpacing))
            DropdownMenuGroup(
                shapes = MenuDefaults.groupShape(1, 2),
                content = content
            )
            Spacer(Modifier.height(MenuDefaults.GroupSpacing))
            ButtonGroup(
                overflowIndicator = { menuState -> ButtonGroupDefaults.OverflowIndicator(menuState) },
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
            ) {
                customItem(
                    buttonGroupContent = {
                        FilledIconButton(
                            onClick = { onChangeSorting(true) },
                            modifier = Modifier
                                .weight(1f)
                                .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = if (isSortedAscending) MenuDefaults.groupVibrantContainerColor else MenuDefaults.groupStandardContainerColor
                            ),
                            shape = if (isSortedAscending) IconButtonDefaults.mediumSelectedSquareShape else IconButtonDefaults.mediumSelectedRoundShape
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.up),
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.mediumIconSize)
                            )
                        }
                    },
                    {}
                )
                customItem(
                    buttonGroupContent = {
                        FilledIconButton(
                            modifier = Modifier
                                .weight(1f)
                                .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)),
                            onClick = { onChangeSorting(false) },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = if (!isSortedAscending) MenuDefaults.groupVibrantContainerColor else MenuDefaults.groupStandardContainerColor
                            ),
                            shape = if (!isSortedAscending) IconButtonDefaults.mediumSelectedSquareShape else IconButtonDefaults.mediumSelectedRoundShape
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.down),
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.mediumIconSize)
                            )
                        }
                    },
                    {}
                )
            }
        }
    }

}