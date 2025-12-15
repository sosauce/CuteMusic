package com.sosauce.cutemusic.presentation.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberAllSafTracks
import com.sosauce.cutemusic.data.datastore.rememberMinTrackDuration
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.presentation.screens.settings.compenents.FoldersView
import com.sosauce.cutemusic.presentation.screens.settings.compenents.SettingsWithTitle
import com.sosauce.cutemusic.presentation.screens.settings.compenents.SliderSettingsCards
import com.sosauce.cutemusic.presentation.shared_components.CuteNavigationButton
import com.sosauce.cutemusic.presentation.shared_components.MusicListItem
import com.sosauce.cutemusic.utils.copyMutate

@Composable
fun SettingsLibrary(
    latestSafTracks: List<CuteTrack>,
    isPlayerReady: Boolean,
    currentMusicUri: String,
    onNavigateUp: () -> Unit
) {

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var safTracks by rememberAllSafTracks()
    var minTrackDuration by rememberMinTrackDuration()

    val safAudioPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        safTracks = safTracks.copyMutate { add(it.toString()) }

        context.contentResolver.takePersistableUriPermission(
            it ?: Uri.EMPTY,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
    }

    Scaffold(
        bottomBar = {
            CuteNavigationButton(onNavigateUp = onNavigateUp)
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
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
                    text = "Min track duration",
                    optionalDescription = R.string.min_track_duration_desc
                )
            }
            FoldersView()
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
                            .fillMaxWidth()
                            .wrapContentWidth(),
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

                    latestSafTracks.fastForEach { safTrack ->
                        MusicListItem(
                            music = safTrack,
                            musicState = MusicState(), // TODO pass real state
                            onShortClick = {},
                            onNavigate = {}, // TODO
                            onHandlePlayerActions = {} // TODO
                        )
                    }
                }
            }
        }
    }
}