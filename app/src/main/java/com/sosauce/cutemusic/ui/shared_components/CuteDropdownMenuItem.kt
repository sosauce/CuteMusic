package com.sosauce.cutemusic.ui.shared_components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * A dropdown menu item with some padding and clipped corners,
 * also adds a visible parameter, used in artist and album details.
 */
@Composable
fun CuteDropdownMenuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    colors: MenuItemColors = MenuDefaults.itemColors(),
    contentPadding: PaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
    interactionSource: MutableInteractionSource? = null,
    visible: Boolean = true
) {

    if (visible) {
        DropdownMenuItem(
            text = text,
            onClick = onClick,
            modifier = modifier
                .padding(horizontal = 2.dp)
                .clip(RoundedCornerShape(12.dp)),
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            enabled = enabled,
            colors = colors,
            contentPadding = contentPadding,
            interactionSource = interactionSource
        )
    }
}