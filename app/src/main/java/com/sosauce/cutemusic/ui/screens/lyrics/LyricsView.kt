@file:OptIn(ExperimentalFoundationApi::class)

package com.sosauce.cutemusic.ui.screens.lyrics

import android.view.WindowManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.data.states.MusicState
import com.sosauce.cutemusic.domain.model.Lyrics
import com.sosauce.cutemusic.main.MainActivity
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel

@Composable
fun LyricsView(
    viewModel: MusicViewModel,
    onHideLyrics: () -> Unit,
    isLandscape: Boolean = false,
    musicState: MusicState
) {
    var currentLyric by remember { mutableStateOf(Lyrics()) }
    val clipboardManager = LocalClipboardManager.current
    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = if (musicState.currentLyrics.indexOf(currentLyric) != -1) musicState.currentLyrics.indexOf(
            currentLyric
        ) else 0
    )
    val context = LocalContext.current
    val a = context.contentResolver

    DisposableEffect(Unit) {
        val window = (context as MainActivity).window

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    LaunchedEffect(currentLyric) {
        val indexOfCurrentLyric = musicState.currentLyrics.indexOf(currentLyric)

        if (indexOfCurrentLyric != -1) {
            lazyListState.animateScrollToItem(musicState.currentLyrics.indexOf(currentLyric))
        }
    }



    Box(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            state = lazyListState
        ) {

            if (musicState.currentLyrics.isEmpty()) {
                item {
                    CuteText(
                        text = viewModel.loadEmbeddedLyrics(),
                    )
                }
            } else {
                itemsIndexed(
                    items = musicState.currentLyrics,
                    key = { _, item -> item.timestamp }
                ) { index, lyric ->

                    val nextTimestamp = remember(index) {
                        if (index < musicState.currentLyrics.size - 1) {
                            musicState.currentLyrics[index + 1].timestamp
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
                                    viewModel.handlePlayerActions(
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


        val horizontalArrangement = if (isLandscape) Arrangement.End else Arrangement.Center
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
            horizontalArrangement = horizontalArrangement,
        ) {
            IconButton(
                onClick = {
                    onHideLyrics()
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null
                )
            }
        }
    }
}

