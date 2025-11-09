package com.sosauce.cutemusic.presentation.screens.artist.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R


@Composable
fun NumberOfAlbums(size: Int) {
    Text(
        text = pluralStringResource(
            R.plurals.albums,
            size,
            size
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 15.dp)
    )
}