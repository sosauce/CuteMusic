package com.sosauce.cutemusic.presentation.screens.album.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.presentation.shared_components.CuteText

@Composable
fun NumberOfTracks(
    size: Int,
    onAddToSelected: (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 15.dp)
    ) {
        CuteText(
            text = pluralStringResource(
                R.plurals.tracks,
                size,
                size
            ),
            color = MaterialTheme.colorScheme.primary,
        )
        if (onAddToSelected != null) {
            IconButton(
                onClick = onAddToSelected
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.PlaylistAdd,
                    contentDescription = stringResource(R.string.add_to_playlist),
                )
            }
        }
    }
}