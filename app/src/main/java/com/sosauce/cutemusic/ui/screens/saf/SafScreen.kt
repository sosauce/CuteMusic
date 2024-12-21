package com.sosauce.cutemusic.ui.screens.saf

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.launch
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberAllSafTracks
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.main.MusicListItem
import com.sosauce.cutemusic.ui.shared_components.AppBar
import com.sosauce.cutemusic.ui.shared_components.CuteText

@Composable
fun SafScreen(
    onNavigateUp: () -> Unit,
    latestSafTracks: List<MediaItem>,
    onNavigate: (Screen) -> Unit,
    onShortClick: (String) -> Unit,
    isPlayerReady: Boolean,
    currentMusicUri: String,
) {

    val context = LocalContext.current
    var safTracks by rememberAllSafTracks()

    val safAudioPicker = rememberLauncherForActivityResult(safActivityContract()) {
        safTracks = safTracks.toMutableSet().apply {
            add(it.toString())
        }

        context.contentResolver.takePersistableUriPermission(
            it ?: Uri.EMPTY,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
    }

    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(R.string.saf_manager),
                showBackArrow = true,
                onPopBackStack = onNavigateUp
            )
        }
    ) { values ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(values)
        ) {
            Card(
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    safAudioPicker.launch()
                }
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(10.dp))
                    CuteText(stringResource(R.string.open_saf))
                }
            }
            Spacer(Modifier.height(10.dp))
            LazyColumn {
                items(
                    items = latestSafTracks.toList(),
                    key = { it.mediaId }
                ) { safTrack ->

                    Column(
                        modifier = Modifier
                            .animateItem()
                            .padding(
                                vertical = 2.dp,
                                horizontal = 4.dp
                            )
                    ) {
                        MusicListItem(
                            onShortClick = { onShortClick(safTrack.mediaId) },
                            music = safTrack,
                            onNavigate = { onNavigate(it) },
                            currentMusicUri = currentMusicUri,
                            showBottomSheet = true,
                            isPlayerReady = isPlayerReady,
                            onDeleteSafTrack = {
                                safTracks = safTracks.toMutableSet().apply {
                                    remove(safTrack.mediaMetadata.extras?.getString("uri"))
                                }
                            }
                        )
                    }

                }
            }
        }


    }

}

private fun safActivityContract() = object : ActivityResultContract<Unit, Uri?>() {
    override fun createIntent(
        context: Context,
        input: Unit,
    ) = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "audio/*"
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return intent?.data
    }

}