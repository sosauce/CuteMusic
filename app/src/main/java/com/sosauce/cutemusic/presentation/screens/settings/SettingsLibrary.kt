package com.sosauce.cutemusic.presentation.screens.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.launch
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberAllBlacklistedFolders
import com.sosauce.cutemusic.data.datastore.rememberAllSafTracks
import com.sosauce.cutemusic.data.datastore.rememberMinTrackDuration
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.data.models.Folder
import com.sosauce.cutemusic.presentation.screens.settings.compenents.FolderItem
import com.sosauce.cutemusic.presentation.screens.settings.compenents.SettingsWithTitle
import com.sosauce.cutemusic.presentation.screens.settings.compenents.SliderSettingsCards
import com.sosauce.cutemusic.presentation.shared_components.SafMusicListItem
import com.sosauce.cutemusic.presentation.shared_components.ScaffoldWithBackArrow
import com.sosauce.cutemusic.utils.copyMutate

@Composable
fun SettingsLibrary(
    folders: List<Folder>,
    latestSafTracks: List<CuteTrack>,
    onShortClick: (String) -> Unit,
    isPlayerReady: Boolean,
    currentMusicUri: String,
    onNavigateUp: () -> Unit
) {

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var blacklistedFolders by rememberAllBlacklistedFolders()
    var safTracks by rememberAllSafTracks()
    var minTrackDuration by rememberMinTrackDuration()

    val safAudioPicker = rememberLauncherForActivityResult(safActivityContract()) {
        safTracks = safTracks.copyMutate { add(it.toString()) }

        context.contentResolver.takePersistableUriPermission(
            it ?: Uri.EMPTY,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
    }

    ScaffoldWithBackArrow(
        backArrowVisible = !scrollState.canScrollBackward,
        onNavigateUp = onNavigateUp
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
                    onValueChange = { minTrackDuration = it.toInt() },
                    topDp = 24.dp,
                    bottomDp = 24.dp,
                    text = "Min track duration",
                    optionalDescription = R.string.min_track_duration_desc
                )
            }

            folders.sortedBy { it.name }
                .groupBy { it.path in blacklistedFolders }
                .toSortedMap(compareByDescending { it })
                .forEach { (isBlacklisted, allFolders) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 34.dp,
                                vertical = 8.dp
                            )
                    ) {
                        Text(
                            text = if (isBlacklisted) stringResource(R.string.blacklisted) else stringResource(
                                R.string.not_blacklisted
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    allFolders.fastForEachIndexed { index, folder ->

                        val topDp by animateDpAsState(
                            targetValue = if (index == 0) 24.dp else 4.dp
                        )
                        val bottomDp by animateDpAsState(
                            targetValue = if (index == allFolders.size - 1) 24.dp else 4.dp
                        )
                        FolderItem(
                            folder = folder.path,
                            topDp = topDp,
                            bottomDp = bottomDp,
                            //modifier = Modifier.animateItem(),
                            actionButton = {
                                if (isBlacklisted) {
                                    IconButton(
                                        onClick = {
                                            blacklistedFolders =
                                                blacklistedFolders.copyMutate { remove(folder.path) }
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.trash_rounded_filled),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                } else {
                                    IconButton(
                                        onClick = {
                                            blacklistedFolders =
                                                blacklistedFolders.copyMutate { add(folder.path) }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Add,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        )
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
                            .fillMaxWidth()
                            .wrapContentWidth(),
                        onClick = safAudioPicker::launch
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
                            Text(stringResource(R.string.open_saf))
                        }
                    }

                    latestSafTracks.fastForEach { safTrack ->
                        Column(
                            modifier = Modifier
                                .padding(
                                    vertical = 2.dp,
                                    horizontal = 4.dp
                                )
                        ) {
                            SafMusicListItem(
                                onShortClick = { onShortClick(safTrack.mediaId) },
                                music = safTrack,
                                currentMusicUri = currentMusicUri,
                                isPlayerReady = isPlayerReady,
                                onDeleteFromSaf = {
                                    safTracks = safTracks.toMutableSet().apply {
                                        remove(safTrack.uri.toString())
                                    }
                                }
                            )
                        }
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