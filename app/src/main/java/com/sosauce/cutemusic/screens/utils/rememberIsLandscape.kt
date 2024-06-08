package com.sosauce.cutemusic.screens.utils

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun rememberIsLandscape(): Boolean {
	val config = LocalConfiguration.current

	return remember(config.orientation) {
		config.orientation == Configuration.ORIENTATION_LANDSCAPE
	}
}
