@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.playing.components

import android.content.Intent
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.core.app.ShareCompat
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.presentation.navigation.Screen
import com.sosauce.cutemusic.presentation.screens.playlists.components.PlaylistPicker
import com.sosauce.cutemusic.presentation.shared_components.DeletionDialog
import com.sosauce.cutemusic.presentation.shared_components.MoreOptions
import com.sosauce.cutemusic.presentation.shared_components.MusicDetailsDialog

@Composable
fun MoreOptionsButton(
    modifier: Modifier = Modifier,
    musicState: MusicState,
    onNavigate: (Screen) -> Unit
) {

    val context = LocalContext.current
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showMoreDialog by remember { mutableStateOf(false) }
    var showPlaylistDialog by remember { mutableStateOf(false) }
    var showDeletionDialog by remember { mutableStateOf(false) }
    val activityResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { }
    val moreOptions = listOf(
        MoreOptions(
            text = { stringResource(R.string.open_eq) },
            onClick = {
                try {
                    val intent =
                        Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL).apply {
                            putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicState.audioSessionAudio)
                            putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.packageName)
                            putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                        }
                    activityResultLauncher.launch(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Unable to open system equalizer", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            icon = R.drawable.eq
        ),
        MoreOptions(
            text = { stringResource(R.string.edit) },
            onClick = {
                showMoreDialog = false
                onNavigate(
                    Screen.MetadataEditor(
                        musicState.track.path,
                        musicState.track.uri.toString()
                    )
                )
            },
            icon = R.drawable.edit_rounded
        ),
        MoreOptions(
            text = { stringResource(R.string.go_to, musicState.track.album) },
            onClick = {
                showMoreDialog = false
                onNavigate(
                    Screen.AlbumsDetails(musicState.track.album)
                )
            },
            icon = androidx.media3.session.R.drawable.media3_icon_album
        ),
        MoreOptions(
            text = { stringResource(R.string.go_to, musicState.track.artist) },
            onClick = {
                showMoreDialog = false
                onNavigate(
                    Screen.ArtistsDetails(musicState.track.artist)
                )
            },
            icon = R.drawable.artist_rounded
        ),
        MoreOptions(
            text = { stringResource(R.string.add_to_playlist) },
            onClick = { showPlaylistDialog = true },
            icon = R.drawable.playlist_add
        )
    )

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

    if (showDeletionDialog) {
        DeletionDialog(
            track = musicState.track,
            onDismissRequest = { showDeletionDialog = false }
        )
    }

    Column(modifier) {
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


        DropdownMenuPopup(
            expanded = showMoreDialog,
            onDismissRequest = { showMoreDialog = false }
        ) {
            DropdownMenuGroup(
                shapes = MenuDefaults.groupShapes()
            ) {
                moreOptions.fastForEachIndexed { index, option ->
                    DropdownMenuItem(
                        onClick = option.onClick,
                        shape = when (index) {
                            0 -> MenuDefaults.leadingItemShape
                            moreOptions.lastIndex -> MenuDefaults.trailingItemShape
                            else -> MenuDefaults.middleItemShape
                        },
                        text = {
                            Text(option.text())
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(option.icon),
                                contentDescription = null
                            )
                        }
                    )
                }
            }
            Spacer(Modifier.height(MenuDefaults.GroupSpacing))
            ButtonGroup(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
            ) {
                FilledIconButton(
                    onClick = { showDetailsDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)),
                    shape = IconButtonDefaults.mediumSquareShape,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.info_filled),
                        contentDescription = null,
                        modifier = Modifier.size(IconButtonDefaults.mediumIconSize)
                    )
                }
                FilledIconButton(
                    onClick = {
                        ShareCompat.IntentBuilder(context)
                            .setType("audio/*")
                            .setStream(Uri.parse(musicState.track.path)) // this instead of passing the path allows to see the file name in the share sheet
                            .setChooserTitle("Share track")
                            .startChooser()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)),
                    shape = IconButtonDefaults.mediumSquareShape,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainer)
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.share_filled),
                        contentDescription = null,
                        modifier = Modifier.size(IconButtonDefaults.mediumIconSize)
                    )
                }
                FilledIconButton(
                    onClick = { showDeletionDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .size(IconButtonDefaults.mediumContainerSize(IconButtonDefaults.IconButtonWidthOption.Wide)),
                    shape = IconButtonDefaults.mediumSquareShape,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.trash_rounded_filled),
                        contentDescription = null,
                        modifier = Modifier.size(IconButtonDefaults.mediumIconSize)
                    )
                }
            }
        }
    }
}