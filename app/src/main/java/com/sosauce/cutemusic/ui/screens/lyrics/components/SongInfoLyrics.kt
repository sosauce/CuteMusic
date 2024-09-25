package com.sosauce.cutemusic.ui.screens.lyrics.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.ui.shared_components.CuteText

@Composable
fun BoxScope.SongInfoLyrics(
    currentTitle: String,
    currentArtists: String,
    currentMusicUri: Uri?,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .align(Alignment.TopStart)
            .height(70.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = currentMusicUri,
            contentDescription = null,
            modifier = Modifier
                .padding(start = 10.dp)
                .size(50.dp)
                .clip(RoundedCornerShape(10.dp))
        )
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            CuteText(
                text = currentTitle
            )
            CuteText(
                text = currentArtists,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
            )

        }
    }

}