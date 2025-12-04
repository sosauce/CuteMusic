@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.playlists.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.data.models.Playlist
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.utils.rememberInteractionSource

@Composable
fun PlaylistHeader(
    playlist: Playlist,
    tracks: List<CuteTrack>,
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
            Text(
                text = playlist.emoji,
                maxLines = 1,
                fontSize = 70.sp
            )
        }

        Spacer(Modifier.height(10.dp))
        Text(
            text = playlist.name,
            style = MaterialTheme.typography.headlineMediumEmphasized,
            maxLines = 1,
            modifier = Modifier.basicMarquee()
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            playlist.tags.fastForEach { tag ->
                AssistChip(
                    onClick = {},
                    label = { Text(tag) }
                )
            }
        }
        Spacer(Modifier.height(15.dp))

        ButtonGroup(
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            IconButton(
                onClick = {
                    onHandlePlayerActions(
                        PlayerActions.Play(
                            index = 0,
                            tracks = tracks
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
                        PlayerActions.Play(
                            index = 0,
                            tracks = tracks,
                            random = true
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
                    painter = painterResource(R.drawable.shuffle),
                    contentDescription = null
                )
            }
        }
    }
}