@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.lyrics

import android.app.SearchManager
import android.content.ClipData
import android.content.Intent
import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.actions.PlayerActions
import com.sosauce.cutemusic.domain.model.Lyrics
import com.sosauce.cutemusic.presentation.screens.playing.components.PlayPauseButton
import com.sosauce.cutemusic.presentation.shared_components.animations.AnimatedIconButton
import com.sosauce.cutemusic.utils.ICON_TEXT_SPACING
import com.sosauce.cutemusic.utils.selfAlignHorizontally
import kotlinx.coroutines.launch

@Composable
fun LyricsScreen(
    onNavigateBack: () -> Unit,
    lyrics: List<Lyrics>,
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {
    val activity = LocalActivity.current
    val context = LocalContext.current
    val clipboardManager = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val currentLyricIndex by remember(musicState.position) {
        derivedStateOf {
            lyrics.indexOfLast { musicState.position >= it.timestamp }
        }
    }
    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = if (currentLyricIndex!= -1) currentLyricIndex else 0
    )

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
                    FloatingToolbarDefaults.VibrantFloatingActionButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.close),
                            contentDescription = stringResource(R.string.close)
                        )
                    }
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
            if (lyrics.isEmpty()) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(stringResource(R.string.no_lyrics_note))
                        Button(
                            onClick = {
                                val query =
                                    "${musicState.track.title} ${musicState.track.artist} lyrics"
                                val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                                    putExtra(SearchManager.QUERY, query)
                                }
                                context.startActivity(intent)
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.open),
                                contentDescription = null
                            )
                            Spacer(Modifier.width(ICON_TEXT_SPACING.dp))
                            Text(stringResource(R.string.search_lyrics))
                        }
                    }
                }
            } else {
                itemsIndexed(
                    items = lyrics,
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
                            style = MaterialTheme.typography.titleLargeEmphasized.copy(color),
                            modifier = Modifier.padding(15.dp)
                        )
                    }
                }
            }
        }
    }
}

