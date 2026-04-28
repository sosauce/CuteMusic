@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.lyrics

import android.app.SearchManager
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.vibrantFloatingToolbarColors
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberLyricsAlignment
import com.sosauce.chocola.data.datastore.rememberLyricsFontSize
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.domain.model.Lyrics
import com.sosauce.chocola.presentation.screens.playing.components.PlayPauseButton
import com.sosauce.chocola.presentation.shared_components.animations.AnimatedFab
import com.sosauce.chocola.presentation.shared_components.animations.AnimatedIconButton
import com.sosauce.chocola.utils.ICON_TEXT_SPACING
import com.sosauce.chocola.utils.rememberInteractionSource
import com.sosauce.chocola.utils.selfAlignHorizontally
import com.sosauce.chocola.utils.toLyricsAlignment
import kotlinx.coroutines.launch

@Composable
fun LyricsScreen(
    onNavigateBack: () -> Unit,
    state: LyricsState,
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit,
    onLoadLrcFile: (Uri) -> Unit
) {
    val activity = LocalActivity.current
    val resources = LocalResources.current
    val context = LocalContext.current
    val clipboardManager = LocalClipboard.current

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ContainedLoadingIndicator()
        }
    } else {
        val scope = rememberCoroutineScope()
        val lyricsAlignment by rememberLyricsAlignment()
        val lyricsFontSize by rememberLyricsFontSize()
        val currentLyricIndex by remember(musicState.position) {
            derivedStateOf {
                state.lyrics.indexOfLast { musicState.position >= it.timestamp }
            }
        }
        val lazyListState = rememberLazyListState(
            initialFirstVisibleItemIndex = if (currentLyricIndex!= -1) currentLyricIndex else 0
        )

        val lyricFilePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->

            if (uri == null) return@rememberLauncherForActivityResult

            if (!uri.toString().endsWith(".lrc")) {
                Toast.makeText(
                    context,
                    resources.getString(R.string.not_a_lyric_file),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                onLoadLrcFile(uri)
            }
        }

        DisposableEffect(Unit) {
            val window = activity?.window

            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            onDispose {
                window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }

        LaunchedEffect(currentLyricIndex) {
            if (currentLyricIndex != -1) {
                lazyListState.animateScrollToItem(currentLyricIndex)
            }
        }

        Scaffold(
            bottomBar = {
                HorizontalFloatingToolbar(
                    expanded = true,
                    colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(),
                    modifier = Modifier
                        .selfAlignHorizontally()
                        .navigationBarsPadding(),
                    floatingActionButton = {
                        AnimatedFab(
                            onClick = onNavigateBack,
                            icon = R.drawable.close,
                            containerColor = vibrantFloatingToolbarColors().fabContainerColor
                        )
                    }
                ) {
                    AnimatedIconButton(
                        onClick = { onHandlePlayerActions(PlayerActions.SeekToPreviousMusic) },
                        icon = R.drawable.skip_previous,
                        contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_back_description)
                    )
                    PlayPauseButton(
                        isPlaying = musicState.isPlaying,
                        onHandlePlayerActions = onHandlePlayerActions
                    )
                    AnimatedIconButton(
                        onClick = { onHandlePlayerActions(PlayerActions.SeekToNextMusic) },
                        icon = R.drawable.skip_next,
                        contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_to_next_description)
                    )
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                state = lazyListState,
                contentPadding = paddingValues
            ) {
                if (state.lyrics.isEmpty()) {
                    item {

                        val interactionSources = List(2) { rememberInteractionSource() }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.no_lyrics_note),
                                style = MaterialTheme.typography.headlineSmallEmphasized.copy(
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(Modifier.height(10.dp))
                            ButtonGroup(
                                modifier = Modifier.padding(horizontal = 30.dp),
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val query =
                                            "${musicState.track.title} ${musicState.track.artist} lyrics"
                                        val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                                            putExtra(SearchManager.QUERY, query)
                                        }
                                        context.startActivity(intent)
                                    },
                                    shape = RoundedCornerShape(topStart = 50.dp, bottomStart = 50.dp, topEnd = 4.dp, bottomEnd = 4.dp),
                                    interactionSource = interactionSources[0],
                                    modifier = Modifier
                                        .weight(1f)
                                        .animateWidth(interactionSources[0])
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.search),
                                        contentDescription = null
                                    )
                                    Spacer(Modifier.width(ICON_TEXT_SPACING.dp))
                                    Text(
                                        text = stringResource(R.string.search),
                                        maxLines = 1
                                    )
                                }
                                Button(
                                    onClick = { lyricFilePicker.launch(arrayOf("*/*")) },
                                    shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 50.dp, bottomEnd = 50.dp),
                                    interactionSource = interactionSources[1],
                                    modifier = Modifier
                                        .weight(1f)
                                        .animateWidth(interactionSources[1])
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.resource_import),
                                        contentDescription = null
                                    )
                                    Spacer(Modifier.width(ICON_TEXT_SPACING.dp))
                                    Text(
                                        text = stringResource(R.string.load),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                } else {
                    itemsIndexed(
                        items = state.lyrics,
                        key = { _, lyric -> lyric.id }
                    ) { index, lyric ->

                        val isCurrentLyric = index == currentLyricIndex

                        val color by animateColorAsState(
                            targetValue = if (isCurrentLyric || lyric.timestamp == 0) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .combinedClickable(
                                    onClick = {
                                        onHandlePlayerActions(
                                            PlayerActions.SeekToSlider(
                                                lyric.timestamp.toLong()
                                            )
                                        )
                                    },
                                    onLongClick = {
                                        scope.launch {
                                            clipboardManager.setClipEntry(
                                                ClipEntry(
                                                    ClipData.newPlainText(
                                                        "Lyrics",
                                                        lyric.lineLyrics
                                                    )
                                                )
                                            )
                                        }
                                    }
                                )
                        ) {
                            Text(
                                text = lyric.lineLyrics,
                                style = MaterialTheme.typography.titleLargeEmphasized.copy(
                                    color = color,
                                    fontSize = lyricsFontSize.sp,
                                    textAlign = lyricsAlignment.toLyricsAlignment()
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(15.dp)
                                    .graphicsLayer {
                                        alpha = if (isCurrentLyric) 1f else 0.3f
                                    },
                            )
                        }
                    }
                }
            }
        }
    }

}

