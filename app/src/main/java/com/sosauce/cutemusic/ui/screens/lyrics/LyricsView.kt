@file:OptIn(ExperimentalFoundationApi::class)

package com.sosauce.cutemusic.ui.screens.lyrics

import android.content.ClipData
import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.model.Lyrics
import com.sosauce.cutemusic.ui.screens.playing.components.PlayPauseButton
import com.sosauce.cutemusic.ui.shared_components.AnimatedIconButton
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.utils.AnimationDirection
import com.sosauce.cutemusic.utils.GOOGLE_SEARCH
import com.sosauce.cutemusic.utils.ICON_TEXT_SPACING
import com.sosauce.cutemusic.utils.rememberAnimatable
import kotlinx.coroutines.launch

@Composable
fun LyricsView(
    onHideLyrics: () -> Unit,
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {
    val activity = LocalActivity.current
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboard.current
    val isLandscape = rememberIsLandscape()
    val scope = rememberCoroutineScope()
    var currentLyric by remember { mutableStateOf(Lyrics()) }
    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = if (musicState.lyrics.indexOf(currentLyric) != -1) musicState.lyrics.indexOf(
            currentLyric
        ) else 0
    )

    DisposableEffect(Unit) {
        val window = activity?.window

        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    LaunchedEffect(currentLyric) {
        val indexOfCurrentLyric = musicState.lyrics.indexOf(currentLyric)
        if (indexOfCurrentLyric != -1) {
            lazyListState.animateScrollToItem(musicState.lyrics.indexOf(currentLyric))
        }
    }

    Scaffold { paddingValues ->
        Box {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                state = lazyListState,
                contentPadding = paddingValues
            ) {
                // A bit wonky but works for now
                if (musicState.lyrics.size <= 1 && musicState.lyrics.first().lineLyrics.isEmpty()) {
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CuteText(stringResource(R.string.no_lyrics_note))
                            Button(
                                onClick = { uriHandler.openUri("$GOOGLE_SEARCH${musicState.title}+${musicState.artist}+lyrics") }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                                    contentDescription = null
                                )
                                Spacer(Modifier.width(ICON_TEXT_SPACING.dp))
                                CuteText(stringResource(R.string.search_lyrics))
                            }
                        }
                    }
                } else {
                    itemsIndexed(
                        items = musicState.lyrics,
                        key = { _, lyric -> lyric.id }
                    ) { index, lyric ->

                        val nextTimestamp = remember(index) {
                            if (index < musicState.lyrics.size - 1) {
                                musicState.lyrics[index + 1].timestamp
                            } else 0
                        }

                        val isCurrentLyric by remember(musicState.position) {
                            derivedStateOf {
                                musicState.position in lyric.timestamp until nextTimestamp
                            }
                        }


                        val color by animateColorAsState(
                            targetValue = if (isCurrentLyric || lyric.timestamp == 0L) {
                                MaterialTheme.colorScheme.onBackground
                            } else {
                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            },
                            label = ""
                        )


                        if (isCurrentLyric) {
                            currentLyric = lyric
                        }


                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .combinedClickable(
                                    onClick = {
                                        onHandlePlayerActions(
                                            PlayerActions.SeekToSlider(
                                                lyric.timestamp
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
                            CuteText(
                                text = lyric.lineLyrics,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(15.dp),
                                color = color
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .navigationBarsPadding(),
                horizontalArrangement = if (isLandscape) Arrangement.End else Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedIconButton(
                    onClick = { onHandlePlayerActions(PlayerActions.SeekToPreviousMusic) },
                    animationDirection = AnimationDirection.LEFT,
                    icon = Icons.Rounded.SkipPrevious,
                    contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_back_description)
                )
                PlayPauseButton(
                    isPlaying = musicState.isPlaying,
                    onHandlePlayerActions = onHandlePlayerActions
                )
                AnimatedIconButton(
                    onClick = { onHandlePlayerActions(PlayerActions.SeekToNextMusic) },
                    animationDirection = AnimationDirection.RIGHT,
                    icon = Icons.Rounded.SkipNext,
                    contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_to_next_description)
                )
                VerticalDivider(
                    modifier = Modifier.height(20.dp)
                )
                IconButton(
                    onClick = onHideLyrics
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.close)
                    )
                }
            }
        }
    }
}

