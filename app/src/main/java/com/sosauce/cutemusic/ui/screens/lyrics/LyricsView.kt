@file:OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)

package com.sosauce.cutemusic.ui.screens.lyrics

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.EaseInBounce
import androidx.compose.animation.core.EaseInQuad
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.times
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sosauce.cutemusic.data.actions.PlayerActions
import com.sosauce.cutemusic.domain.model.Lyrics
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

@Composable
fun LyricsView(
    viewModel: MusicViewModel,
    onHideLyrics: () -> Unit,
    path: String,
    isLandscape: Boolean = false
) {
    var currentLyric by remember { mutableStateOf(Lyrics()) }
    val clipboardManager = LocalClipboardManager.current
    val indexToScrollTo = viewModel.currentLyrics.indexOfFirst { lyric ->
        viewModel.currentPosition in lyric.timestamp until (viewModel.currentLyrics.getOrNull(
            viewModel.currentLyrics.indexOf(lyric) + 1
        )?.timestamp ?: 0)
    }
    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = if (indexToScrollTo != -1) indexToScrollTo else 0
    )
    val firstVisibleIndex by remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }



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

            if (viewModel.currentLyrics.isEmpty()) {
                item {
                    CuteText(
                        text = viewModel.loadEmbeddedLyrics(path),
                    )
                }
            } else {
                itemsIndexed(
                    items = viewModel.currentLyrics,
                    key = { _, item -> item.timestamp }
                ) { index, lyric ->


                    val nextTimestamp = remember(index, viewModel.currentLyrics) {
                        if (index < viewModel.currentLyrics.size - 1) {
                            viewModel.currentLyrics[index + 1].timestamp
                        } else {
                            0
                        }
                    }

                    val isCurrentLyric = remember(viewModel.currentPosition, nextTimestamp) {
                        viewModel.currentPosition in lyric.timestamp until nextTimestamp
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
                        LaunchedEffect(Unit) {
                            lazyListState.animateScrollToItem(index)
                        }
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

