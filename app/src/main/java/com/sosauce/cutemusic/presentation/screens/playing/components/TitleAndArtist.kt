@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.playing.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.sosauce.cutemusic.data.states.MusicState


@Composable
fun TitleAndArtist(
    titleModifier: Modifier = Modifier,
    musicState: MusicState
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = musicState.title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMediumEmphasized,
            fontWeight = FontWeight.ExtraBold,
            modifier = titleModifier
                .fillMaxWidth()
                .basicMarquee()
        )
        Text(
            text = musicState.artist,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleLargeEmphasized,
            modifier = Modifier.basicMarquee()
        )
    }
}