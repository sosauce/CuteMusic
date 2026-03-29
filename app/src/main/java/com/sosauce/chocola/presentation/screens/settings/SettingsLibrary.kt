package com.sosauce.chocola.presentation.screens.settings

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastMap
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberAllSafTracks
import com.sosauce.chocola.data.datastore.rememberMinTrackDuration
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.navigation.Screen
import com.sosauce.chocola.presentation.screens.settings.compenents.FoldersView
import com.sosauce.chocola.presentation.screens.settings.compenents.SettingsWithTitle
import com.sosauce.chocola.presentation.screens.settings.compenents.SliderSettingsCards
import com.sosauce.chocola.presentation.shared_components.CuteNavigationButton
import com.sosauce.chocola.presentation.shared_components.MusicListItem
import com.sosauce.chocola.utils.copyMutate
import com.sosauce.chocola.utils.selfAlignHorizontally

@Composable
fun SettingsLibrary(
    safTracksUi: List<CuteTrack>,
    hiddenTracks: List<CuteTrack>,
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onNavigate: (Screen) -> Unit,
    onNavigateUp: () -> Unit,
    onUnhideTrack: (String) -> Unit
) {

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var safTracks by rememberAllSafTracks()
    var minTrackDuration by rememberMinTrackDuration()

    val safAudioPicker =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            safTracks = safTracks.copyMutate { addAll(uris.fastMap { it.toString() }) }

            uris.fastForEach { uri ->
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
        }

    Scaffold(
        bottomBar = {
            CuteNavigationButton(onNavigateUp = onNavigateUp)
        }
    ) { pv ->
        Column(
            modifier = Modifier
                //.verticalScroll(scrollState)
                .padding(pv)
        ) {

            SettingsWithTitle(
                title = R.string.scan
            ) {
                SliderSettingsCards(
                    value = minTrackDuration,
                    onValueChange = { minTrackDuration = it },
                    topDp = 24.dp,
                    bottomDp = 24.dp,
                    text = R.string.min_track_length_text,
                    optionalDescription = R.string.min_track_duration_desc
                )
            }
            FoldersView()
            SettingsWithTitle(
                title = R.string.hidden_tracks
            ) {
                Card(
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    if (hiddenTracks.isNotEmpty()) {
                        hiddenTracks.fastForEach { track ->
                            MusicListItem(
                                track = track,
                                musicState = musicState,
                                onShortClick = { onUnhideTrack(track.mediaId) },
                                onNavigate = onNavigate,
                                onHandlePlayerActions = onHandlePlayerActions
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.hide),
                                contentDescription = null,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "No hidden tracks!",
                                style = MaterialTheme.typography.bodyMediumEmphasized.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
            SettingsWithTitle(
                title = R.string.saf_manager
            ) {
                Card(
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .padding(5.dp)
                            .selfAlignHorizontally(),
                        onClick = { safAudioPicker.launch(arrayOf("audio/*")) }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.open),
                                contentDescription = null
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(stringResource(R.string.open_saf))
                        }
                    }

                    safTracksUi.fastForEachIndexed { index, safTrack ->
                        MusicListItem(
                            track = safTrack,
                            musicState = musicState,
                            onShortClick = {
                                onHandlePlayerActions(
                                    PlayerActions.Play(
                                        index,
                                        safTracksUi
                                    )
                                )
                            },
                            onNavigate = onNavigate,
                            onHandlePlayerActions = onHandlePlayerActions
                        )
                    }
                }
            }
        }
    }
}