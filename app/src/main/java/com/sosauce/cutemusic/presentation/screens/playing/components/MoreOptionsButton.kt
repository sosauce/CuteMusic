@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.playing.components

import android.content.Intent
import android.media.audiofx.AudioEffect
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.presentation.navigation.Screen
import com.sosauce.cutemusic.presentation.screens.playlists.components.PlaylistPicker
import com.sosauce.cutemusic.presentation.shared_components.CuteDropdownMenuItem
import com.sosauce.cutemusic.presentation.shared_components.MusicDetailsDialog

@Composable
fun MoreOptionsButton(
    musicState: MusicState,
    onNavigate: (Screen) -> Unit
) {

    val context = LocalContext.current
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showMoreDialog by remember { mutableStateOf(false) }
    var showPlaylistDialog by remember { mutableStateOf(false) }
    val activityResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { }

    if (showDetailsDialog) {
        MusicDetailsDialog(
            track = musicState.track,
            onDismissRequest = { showDetailsDialog = false }
        )
    }

    if (showPlaylistDialog) {
        PlaylistPicker(
            mediaId = listOf(musicState.track.mediaId),
            onDismissRequest = { showPlaylistDialog = false }
        )
    }

    Column {
        IconButton(
            onClick = { showMoreDialog = true },
            shapes = IconButtonDefaults.shapes(),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
            ),
            modifier = Modifier
                .size(IconButtonDefaults.smallContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide))
        ) {
            Icon(
                painter = painterResource(R.drawable.more_vert),
                contentDescription = null
            )
        }
        Spacer(Modifier.height(2.dp))
        DropdownMenu(
            expanded = showMoreDialog,
            onDismissRequest = { showMoreDialog = false },
            shape = RoundedCornerShape(24.dp)
        ) {
            CuteDropdownMenuItem(
                onClick = {
                    val intent =
                        Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL).apply {
                            putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicState.audioSessionAudio)
                            putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.packageName)
                            putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                        }
                    activityResultLauncher.launch(intent)
                },
                text = {
                    Text(stringResource(R.string.open_eq))
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.eq),
                        contentDescription = null
                    )
                }
            )
            CuteDropdownMenuItem(
                onClick = { showDetailsDialog = true },
                text = {
                    Text(stringResource(R.string.details))
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.info_rounded),
                        contentDescription = null
                    )
                }
            )
            CuteDropdownMenuItem(
                onClick = {
                    showMoreDialog = false
                    onNavigate(Screen.AlbumsDetails(musicState.track.album))
                },
                text = {
                    Text("${stringResource(R.string.go_to)} ${musicState.track.album}")
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(androidx.media3.session.R.drawable.media3_icon_album),
                        contentDescription = null
                    )
                }
            )
            CuteDropdownMenuItem(
                onClick = {
                    showMoreDialog = false
                    onNavigate(Screen.ArtistsDetails(musicState.track.artist))
                },
                text = {
                    Text("${stringResource(R.string.go_to)} ${musicState.track.artist}")
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.artist_rounded),
                        contentDescription = null
                    )
                }
            )
            CuteDropdownMenuItem(
                onClick = { showPlaylistDialog = true },
                text = {
                    Text(stringResource(R.string.add_to_playlist))
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.playlist_add),
                        contentDescription = null
                    )
                }
            )
            CuteDropdownMenuItem(
                onClick = {
                    context.startActivity(
                        Intent.createChooser(
                            Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_STREAM, musicState.track.uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                type = "audio/*"
                            }, null
                        )
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.share)
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(androidx.media3.session.R.drawable.media3_icon_share),
                        contentDescription = null
                    )
                }
            )
        }
    }

}