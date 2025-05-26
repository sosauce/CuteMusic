package com.sosauce.cutemusic.ui.shared_components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.ui.screens.playlists.PlaylistPicker
import com.sosauce.cutemusic.utils.rememberSearchbarMaxFloatValue
import com.sosauce.cutemusic.utils.rememberSearchbarRightPadding

@Composable
fun SelectedBar(
    modifier: Modifier = Modifier,
    selectedElements: List<String>,
    onClearSelected: () -> Unit
) {

    var showPlaylistDialog by remember { mutableStateOf(false) }

    if (showPlaylistDialog) {
        PlaylistPicker(
            mediaId = selectedElements,
            onDismissRequest = { showPlaylistDialog = false },
            onAddingFinished = onClearSelected
        )
    }

    Column(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxWidth(rememberSearchbarMaxFloatValue())
            .padding(end = rememberSearchbarRightPadding())
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onClearSelected
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null
                )
            }
            CuteText(selectedElements.size.toString())
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { showPlaylistDialog = true }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.PlaylistAdd,
                    contentDescription = null
                )
            }

            IconButton(
                onClick = {}
            ) {
                Icon(
                    painter = painterResource(R.drawable.trash_rounded_filled),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}