@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.playing.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.presentation.shared_components.CuteText

@Composable
fun TitleAndArtist(
    titleModifier: Modifier = Modifier,
    musicState: MusicState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        CuteText(
            text = musicState.title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMediumEmphasized,
            fontWeight = FontWeight.ExtraBold,
            modifier = titleModifier
                .fillMaxWidth()
                .basicMarquee()
        )
        CuteText(
            text = musicState.artist,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleLargeEmphasized,
            modifier = Modifier.basicMarquee()
        )
    }
}