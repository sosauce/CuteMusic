@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.playlists.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.presentation.shared_components.CuteText

@Composable
fun EmptyPlaylist(emoji: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (emoji.isEmpty()) {
            Icon(
                painter = painterResource(R.drawable.playlist),
                contentDescription = null,
                modifier = Modifier.size(70.dp)
            )
        } else {
            CuteText(
                text = emoji,
                fontSize = 70.sp
            )
        }
        Spacer(Modifier.height(10.dp))
        CuteText(
            text = stringResource(R.string.its_empty_here),
            style = MaterialTheme.typography.headlineMediumEmphasized,
            fontWeight = FontWeight.Black
        )
        CuteText(
            text = stringResource(R.string.empty_playlist_desc),
            style = MaterialTheme.typography.bodyMediumEmphasized,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}