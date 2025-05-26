package com.sosauce.cutemusic.ui.screens.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberAllBlacklistedFolders
import com.sosauce.cutemusic.data.datastore.rememberAllSafTracks
import com.sosauce.cutemusic.domain.model.Folder
import com.sosauce.cutemusic.ui.screens.settings.compenents.FolderItem
import com.sosauce.cutemusic.ui.shared_components.CuteNavigationButton
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.SafMusicListItem
import com.sosauce.cutemusic.utils.copyMutate
import com.sosauce.cutemusic.utils.showCuteSearchbar

@Composable
fun SettingsLibrary(
    folders: List<Folder>,
    latestSafTracks: List<MediaItem>,
    onShortClick: (String) -> Unit,
    isPlayerReady: Boolean,
    currentMusicUri: String,
    onNavigateUp: () -> Unit
) {

    val context = LocalContext.current
    val state = rememberLazyListState()
    var blacklistedFolders by rememberAllBlacklistedFolders()
    var safTracks by rememberAllSafTracks()

    val safAudioPicker = rememberLauncherForActivityResult(safActivityContract()) {
        safTracks = safTracks.copyMutate { add(it.toString()) }

        context.contentResolver.takePersistableUriPermission(
            it ?: Uri.EMPTY,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
    }

    Scaffold { paddingValues ->
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                contentPadding = paddingValues,
                state = state
            ) {
                folders.sortedBy { it.name }
                    .groupBy { it.path in blacklistedFolders }
                    .toSortedMap(compareByDescending { it })
                    .forEach { (isBlacklisted, allFolders) ->
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 34.dp,
                                        vertical = 8.dp
                                    )
                            ) {
                                CuteText(
                                    text = if (isBlacklisted) stringResource(R.string.blacklisted) else stringResource(
                                        R.string.not_blacklisted
                                    ),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        itemsIndexed(
                            items = allFolders,
                            key = { _, folder -> folder.path }
                        ) { index, folder ->
                            val topDp by animateDpAsState(
                                targetValue = if (index == 0) 24.dp else 4.dp,
                                label = "Top Dp"
                            )
                            val bottomDp by animateDpAsState(
                                targetValue = if (index == allFolders.size - 1) 24.dp else 4.dp,
                                label = "Bottom Dp"
                            )

                            FolderItem(
                                folder = folder.path,
                                topDp = topDp,
                                bottomDp = bottomDp,
                                modifier = Modifier.animateItem(),
                                actionButton = {
                                    if (isBlacklisted) {
                                        IconButton(
                                            onClick = {
                                                blacklistedFolders = blacklistedFolders.copyMutate { remove(folder.path) }
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
                                                blacklistedFolders = blacklistedFolders.copyMutate { add(folder.path) }
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
                item {
                    HorizontalDivider(Modifier.padding(10.dp))
                }
                item {
                    Card(
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
                        modifier = Modifier
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
                            CuteText(stringResource(R.string.open_saf))
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
                items(
                    items = latestSafTracks,
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
                        SafMusicListItem(
                            onShortClick = { onShortClick(safTrack.mediaId) },
                            music = safTrack,
                            currentMusicUri = currentMusicUri,
                            isPlayerReady = isPlayerReady,
                            onDeleteFromSaf = {
                                safTracks = safTracks.toMutableSet().apply {
                                    remove(safTrack.mediaMetadata.extras?.getString("uri"))
                                }
                            }
                        )
                    }

                }
            }
            AnimatedVisibility(
                visible = state.showCuteSearchbar,
                modifier = Modifier
                    .padding(start = 15.dp)
                    .align(Alignment.BottomStart),
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                CuteNavigationButton(
                    modifier = Modifier.navigationBarsPadding()
                ) { onNavigateUp() }
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