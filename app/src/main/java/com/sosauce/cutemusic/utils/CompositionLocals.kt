@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.sosauce.cutemusic.utils

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation3.runtime.NavKey

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope> { throw IllegalStateException("No SharedTransitionScope provided") }
val LocalScreen = compositionLocalOf<NavKey> { throw IllegalStateException("Can't determine current screen!") }