@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.shared_components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.PlaylistRemove
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.presentation.screens.playlists.components.PlaylistPicker

/**
 * A dropdown menu item with some padding and clipped corners,
 * also adds a visible parameter, if needed.
 */
@Composable
fun CuteDropdownMenuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
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
        )
    }
}

@Composable
fun AddToPlaylistDropdownItem(
    music: CuteTrack
) {

    var showPlaylistDialog by remember { mutableStateOf(false) }
    if (showPlaylistDialog) {
        PlaylistPicker(
            mediaId = listOf(music.mediaId),
            onDismissRequest = { showPlaylistDialog = false }
        )
    }


    CuteDropdownMenuItem(
        onClick = { showPlaylistDialog = true },
        text = {
            Text(stringResource(R.string.add_to_playlist))
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.PlaylistAdd,
                contentDescription = null
            )
        }
    )
}

@Composable
fun RemoveFromPlaylistDropdownItem(
    onRemoveFromPlaylist: () -> Unit
) {
    CuteDropdownMenuItem(
        onClick = onRemoveFromPlaylist,
        text = {
            Text(stringResource(R.string.remove_from_playlist))
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.PlaylistRemove,
                contentDescription = null
            )
        }
    )
}
