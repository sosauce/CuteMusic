@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.playlists.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.domain.model.Playlist
import com.sosauce.cutemusic.presentation.shared_components.CuteText

@Composable
fun PlaylistHeaderLandscape(
    playlist: Playlist,
    musics: List<MediaItem>,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (playlist.emoji.isEmpty()) {
            Icon(
                painter = painterResource(R.drawable.playlist),
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )
        } else {
            CuteText(
                text = playlist.emoji,
                maxLines = 1,
                modifier = Modifier.size(200.dp)
            )
        }
        Spacer(Modifier.width(15.dp))
        Column {
            CuteText(
                text = playlist.name,
                style = MaterialTheme.typography.headlineMediumEmphasized,
                modifier = Modifier.basicMarquee()
            )
            Spacer(Modifier.height(15.dp))
            IconButton(
                onClick = {
                    onHandlePlayerActions(
                        PlayerActions.StartPlaylistPlayback(
                            playlistSongsId = playlist.musics,
                            mediaId = musics.first().mediaId
                        )
                    )
                },
                shapes = IconButtonDefaults.shapes(),
                colors = IconButtonDefaults.filledIconButtonColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
            ) {
                Icon(
                    painter = painterResource(R.drawable.widget_play),
                    contentDescription = null
                )
            }
            Spacer(Modifier.height(5.dp))
            IconButton(
                onClick = {
                    onHandlePlayerActions(
                        PlayerActions.StartPlaylistPlayback(
                            playlistSongsId = playlist.musics,
                            mediaId = null
                        )
                    )
                },
                shapes = IconButtonDefaults.shapes(),
                colors = IconButtonDefaults.filledIconButtonColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
            ) {
                Icon(
                    imageVector = Icons.Rounded.Shuffle,
                    contentDescription = null
                )
            }
        }
    }
}