package com.sosauce.cutemusic.ui.shared_components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String,
    showBackArrow: Boolean,
    onPopBackStack: (() -> Unit)? = null,
) {
    TopAppBar(
        title = {
            CuteText(
                text = title,
                maxLines = 1
            )
        },
        navigationIcon = {
            if (showBackArrow) {
                IconButton(onClick = { onPopBackStack?.invoke() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back arrow"
                    )
                }
            }
        }
    )
}
