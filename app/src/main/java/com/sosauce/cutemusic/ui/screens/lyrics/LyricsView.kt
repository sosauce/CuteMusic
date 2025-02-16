@file:OptIn(ExperimentalFoundationApi::class)

package com.sosauce.cutemusic.ui.screens.lyrics

import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.model.Lyrics
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.utils.ICON_TEXT_SPACING
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LyricsView(
    lyrics: List<Lyrics>,
    onHideLyrics: () -> Unit,
    isLandscape: Boolean = false,
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val leftIconOffsetX = remember { Animatable(0f) }
    val rightIconOffsetX = remember { Animatable(0f) }
    var currentLyric by remember { mutableStateOf(Lyrics()) }
    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = if (lyrics.indexOf(currentLyric) != -1) lyrics.indexOf(
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
        val indexOfCurrentLyric = lyrics.indexOf(currentLyric)
        if (indexOfCurrentLyric != -1) {
            lazyListState.animateScrollToItem(lyrics.indexOf(currentLyric))
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
                if (lyrics.first().lineLyrics == context.getString(R.string.no_lyrics_note)) {
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CuteText(
                                text = lyrics.first().lineLyrics
                            )
                            Button(
                                onClick = { uriHandler.openUri("https://www.google.com/search?q=${musicState.currentlyPlaying}+${musicState.currentArtist}+lyrics") }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                                    contentDescription = null
                                )
                                Spacer(Modifier.width(ICON_TEXT_SPACING.dp))
                                CuteText("Search for lyrics")
                            }
                        }
                    }
                } else {
                    itemsIndexed(
                        items = lyrics,
                        key = { _, item -> item.timestamp }
                    ) { index, lyric ->

                        val nextTimestamp = remember(index) {
                            if (index < lyrics.size - 1) {
                                lyrics[index + 1].timestamp
                            } else {
                                0
                            }
                        }

                        val isCurrentLyric by remember(musicState.currentPosition) {
                            derivedStateOf {
                                musicState.currentPosition in lyric.timestamp until nextTimestamp
                            }
                        }


                        val color by animateColorAsState(
                            targetValue = if (isCurrentLyric) {
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
                                        clipboardManager.setText(
                                            AnnotatedString(
                                                text = lyric.lineLyrics
                                            )
                                        )
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
                    ),
                horizontalArrangement = if (isLandscape) Arrangement.End else Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        onHandlePlayerActions(PlayerActions.SeekToPreviousMusic)
                        scope.launch(Dispatchers.Main) {
                            leftIconOffsetX.animateTo(
                                targetValue = -20f,
                                animationSpec = tween(250)
                            )
                            leftIconOffsetX.animateTo(
                                targetValue = 0f,
                                animationSpec = tween(250)
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SkipPrevious,
                        contentDescription = null,
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    x = leftIconOffsetX.value.toInt(),
                                    y = 0
                                )
                            }
                    )
                }
                IconButton(
                    onClick = { onHandlePlayerActions(PlayerActions.PlayOrPause) }
                ) {
                    Icon(
                        imageVector = if (musicState.isCurrentlyPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = null
                    )
                }
                IconButton(
                    onClick = {
                        onHandlePlayerActions(PlayerActions.SeekToNextMusic)
                        scope.launch(Dispatchers.Main) {
                            rightIconOffsetX.animateTo(
                                targetValue = 20f,
                                animationSpec = tween(250)
                            )
                            rightIconOffsetX.animateTo(
                                targetValue = 0f,
                                animationSpec = tween(250)
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SkipNext,
                        contentDescription = null,
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    x = rightIconOffsetX.value.toInt(),
                                    y = 0
                                )
                            }
                    )
                }
                VerticalDivider(
                    modifier = Modifier.height(20.dp)
                )
                IconButton(
                    onClick = onHideLyrics
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

