package com.sosauce.cutemusic.ui.screens.playing.components

import android.content.Context
import android.content.Intent
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.MusicState
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicStateDetailsDialog
import com.sosauce.cutemusic.utils.CUTE_MUSIC_ID

@Composable
fun QuickActionsRow(
    musicState: MusicState,
    onShowLyrics: () -> Unit,
    onShowSpeedCard: () -> Unit,
    onChargeAlbumSongs: (String) -> Unit,
    onNavigate: (Screen) -> Unit,
    onChargeArtistLists: (String) -> Unit,
    onHandlePlayerActions: (PlayerActions) -> Unit,
) {
    val context = LocalContext.current
    var isDropDownExpanded by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    val uri = remember { Uri.parse(musicState.currentMusicUri) }
    var showTimePicker by remember { mutableStateOf(false) }
    val onBackground = MaterialTheme.colorScheme.onBackground
    val eqIntent = rememberLauncherForActivityResult(equalizerActivityContract()) { }

    if (showDetailsDialog) {
        MusicStateDetailsDialog(
            musicState = musicState,
            onDismissRequest = { showDetailsDialog = false }
        )
    }

    if (showTimePicker) {
        CuteTimePicker(
            onDismissRequest = { showTimePicker = false },
            onSetTimer = { hours, minutes ->
                showTimePicker = false
                onHandlePlayerActions(PlayerActions.SetSleepTimer(hours, minutes))
            },
            initialMillis = musicState.sleepTimer
        )
    }



    Column {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {

            IconButton(onClick = onShowLyrics) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Article,
                    contentDescription = "show lyrics"
                )
            }

            IconButton(onClick = onShowSpeedCard) {
                Icon(
                    imageVector = Icons.Rounded.Speed,
                    contentDescription = "change speed"
                )
            }
            IconButton(
                onClick = { showTimePicker = true }
            ) {
                Box {
                    Icon(
                        painter = painterResource(R.drawable.bedtime_outlined),
                        contentDescription = "set sleep timer"
                    )
                    if (musicState.sleepTimer > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .drawBehind { drawCircle(onBackground) }
                                .size(8.dp)
                        )
                    }
                }
            }
            Row {
                IconButton(onClick = { isDropDownExpanded = true }) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "more"
                    )
                }


                DropdownMenu(
                    expanded = isDropDownExpanded,
                    onDismissRequest = { isDropDownExpanded = false },
                    shape = RoundedCornerShape(24.dp)
                ) {
                    DropdownMenuItem(
                        onClick = {
                            try {
                                eqIntent.launch()
                            } catch (e: Exception) {
                                Log.d(
                                    "CuteError",
                                    "Couldn't open EQ: ${e.stackTrace}, ${e.message}"
                                )
                            }
                        },
                        text = {
                            CuteText(stringResource(R.string.open_eq))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                                contentDescription = null
                            )
                        }
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .align(Alignment.CenterHorizontally)
                    )
                    DropdownMenuItem(
                        onClick = { showDetailsDialog = true },
                        text = {
                            CuteText(stringResource(R.string.details))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.ErrorOutline,
                                contentDescription = null,
                                modifier = Modifier.rotate(180f)
                            )
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            isDropDownExpanded = false
                            onChargeAlbumSongs(musicState.currentAlbum)
                            onNavigate(Screen.AlbumsDetails(musicState.currentAlbumId))
                        },
                        text = {
                            CuteText("${stringResource(R.string.go_to)} ${musicState.currentAlbum}")
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(androidx.media3.session.R.drawable.media3_icon_album),
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            isDropDownExpanded = false
                            onChargeArtistLists(musicState.currentArtist)
                            onNavigate(Screen.ArtistsDetails(musicState.currentArtistId))
                        },
                        text = {
                            CuteText("${stringResource(R.string.go_to)} ${musicState.currentArtist}")
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.artist_rounded),
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        onClick = {
                            context.startActivity(
                                Intent.createChooser(
                                    Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        type = "audio/*"
                                    }, null
                                )
                            )
                        },
                        text = {
                            CuteText(
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
    }
}

private fun equalizerActivityContract() = object : ActivityResultContract<Unit, Unit>() {
    override fun createIntent(
        context: Context,
        input: Unit,
    ) = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL).apply {
        putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.packageName)
        putExtra(AudioEffect.EXTRA_AUDIO_SESSION, CUTE_MUSIC_ID)
        putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
    }

    override fun parseResult(
        resultCode: Int,
        intent: Intent?,
    ) {
    }
}