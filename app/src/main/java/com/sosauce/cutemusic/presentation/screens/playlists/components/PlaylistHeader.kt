@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.playlists.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.ButtonGroup
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
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.domain.model.Playlist
import com.sosauce.cutemusic.presentation.shared_components.CuteText
import com.sosauce.cutemusic.utils.rememberInteractionSource

@Composable
fun PlaylistHeader(
    playlist: Playlist,
    musics: List<MediaItem>,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {

    val interactionSources = List(2) { rememberInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (playlist.emoji.isEmpty()) {
            Icon(
                painter = painterResource(R.drawable.playlist),
                contentDescription = null,
                modifier = Modifier.size(70.dp)
            )
        } else {
            CuteText(
                text = playlist.emoji,
                maxLines = 1,
                fontSize = 70.sp
            )
        }

        Spacer(Modifier.height(10.dp))
        CuteText(
            text = playlist.name,
            style = MaterialTheme.typography.headlineMediumEmphasized,
            maxLines = 1,
            modifier = Modifier.basicMarquee()
        )
        Spacer(Modifier.height(15.dp))

        ButtonGroup(
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
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
                interactionSource = interactionSources[0],
                modifier = Modifier
                    .weight(1f)
                    .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
                    .animateWidth(interactionSources[0])
            ) {
                Icon(
                    painter = painterResource(R.drawable.widget_play),
                    contentDescription = null
                )
            }
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
                interactionSource = interactionSources[1],
                modifier = Modifier
                    .weight(1f)
                    .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
                    .animateWidth(interactionSources[1])
            ) {
                Icon(
                    imageVector = Icons.Rounded.Shuffle,
                    contentDescription = null
                )
            }
        }
    }
}