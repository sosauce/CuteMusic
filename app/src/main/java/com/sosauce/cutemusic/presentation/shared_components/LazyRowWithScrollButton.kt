package com.sosauce.cutemusic.presentation.shared_components

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.sosauce.cutemusic.R
import kotlinx.coroutines.launch

@Composable
fun <T> LazyRowWithScrollButton(
    items: List<T>,
    content: @Composable (T) -> Unit
) {
    val state = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Box {
        LazyRow(
            state = state
        ) {
            items(
                items = items,
                key = { it.hashCode() }
            ) { type ->
                content(type)
            }
        }
        androidx.compose.animation.AnimatedVisibility(
            visible = state.canScrollForward,
            modifier = Modifier.align(Alignment.CenterEnd),
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it }
        ) {
            IconButton(
                onClick = {
                    scope.launch {
                        state.animateScrollToItem(items.lastIndex)
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_right),
                    contentDescription = null
                )
            }
        }
    }
}