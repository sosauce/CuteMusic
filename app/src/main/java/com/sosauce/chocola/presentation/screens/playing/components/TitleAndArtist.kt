@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.playing.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.skydoves.cloudy.cloudy
import com.sosauce.chocola.data.datastore.rememberShowAlbumName
import com.sosauce.chocola.data.states.MusicState


@Composable
fun TitleAndArtist(
    titleModifier: Modifier = Modifier,
    musicState: MusicState
) {

    val showAlbumName by rememberShowAlbumName()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
    ) {
        AnimatedContent(
            modifier = Modifier.fillMaxWidth(),
            targetState = musicState.track.title,
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) {
            val radius by animateIntAsState(
                targetValue = if (transition.isRunning) 15 else 0
            )
            Text(
                text = it,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineMediumEmphasized,
                fontWeight = FontWeight.ExtraBold,
                modifier = titleModifier
                    .fillMaxWidth()
                    .cloudy(radius)
                    .basicMarquee()
            )
        }
        AnimatedContent(
            modifier = Modifier.fillMaxWidth(),
            targetState = musicState.track.artist to musicState.track.album,
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { (artist, album) ->
            val radius by animateIntAsState(
                targetValue = if (transition.isRunning) 15 else 0
            )
            Text(
                text = buildString {
                    append(artist)
                    if (showAlbumName) {
                        append(" - ")
                        append(album)
                    }
                },
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleLargeEmphasized,
                modifier = Modifier
                    .fillMaxWidth()
                    .cloudy(radius)
                    .basicMarquee()
            )
        }
    }
}