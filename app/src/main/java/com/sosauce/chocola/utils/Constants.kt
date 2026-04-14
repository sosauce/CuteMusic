@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.utils

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.style.Style
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.presentation.screens.playing.components.ClassicThumb
import com.sosauce.chocola.presentation.screens.playing.components.CuteSliderState
import com.sosauce.chocola.presentation.screens.playing.components.MorphingThumb
import com.sosauce.chocola.presentation.screens.playing.components.StraightThumb
import com.sosauce.chocola.presentation.screens.playing.components.StraightTrack
import com.sosauce.chocola.presentation.screens.playing.components.WavyTrack

const val CUTE_MUSIC_ID = "CUTE_MUSIC_ID"
const val PACKAGE = "com.sosauce.chocola"
const val ROOT_ID = "cute_music_root"
const val ICON_TEXT_SPACING = 5
const val WIDGET_NEW_DATA = "WIDGET_NEW_DATA"
const val WIDGET_NEW_IS_PLAYING = "WIDGET_NEW_IS_PLAYING"
const val WIDGET_ACTION_BROADCAST = "WIDGET_NEW_DATA"
const val EQUALIZER_ACTION_BROADCAST = "EQUALIZER_ACTION_BROADCAST"
const val GITHUB_RELEASES = "https://github.com/sosauce/Chocola/releases"
const val SUPPORT_PAGE = "https://sosauce.github.io/support/"


object SharedTransitionKeys {
    const val CURRENTLY_PLAYING = "CURRENTLY_PLAYING"
    const val ARTIST = "ARTIST"
    const val PLAY_PAUSE_BUTTON = "PLAY_PAUSE_BUTTON"
    const val FAB = "FAB"
    const val SKIP_NEXT_BUTTON = "SKIP_NEXT_BUTTON"
    const val SKIP_PREVIOUS_BUTTON = "SKIP_PREVIOUS_BUTTON"
    const val MUSIC_ARTWORK = "MUSIC_ARTWORK"
    const val NOW_PLAYING_SCREEN = "NOW_PLAYING_SCREEN"
}


object CuteTheme {
    const val SYSTEM = "SYSTEM"
    const val DARK = "DARK"
    const val LIGHT = "LIGHT"
    const val AMOLED = "AMOLED"
}

object ArtworkShape {
    const val CLASSIC = "classic"
    const val CIRCLE = "circle"
    const val COOKIE_4 = "cookie4"
    const val COOKIE_9 = "cookie9"
    const val COOKIE_12 = "cookie12"
    const val CLOVER_8 = "clover8"
    const val SUNNY = "sunny"
    const val ARROW = "arrow"
    const val DIAMOND = "diamond"
    const val BUN = "bun"
    const val HEART = "heart"
}

object CutePaletteStyle {
    const val EXPRESSIVE = "Expressive"
    const val FIDELITY = "Fidelity"
    const val TONAL_SPOT = "Tonal spot"
    const val NEUTRAL = "Neutral"
    const val VIBRANT = "Vibrant"
    const val MONOCHROME = "Monochrome"
    const val FRUIT_SALAD = "Fruit salad"
}

object LyricsAlignment {
    const val START = "Start"
    const val CENTERED = "Centered"
    const val END = "End"
}

object ThumbStyle {
    const val STRAIGHT = "Straight"
    const val BALL = "Ball"
    const val MORPHING = "Morphing"


    @Composable
    fun toThumb(
        style: String,
        isDragging: Boolean
    ) {
        when(style) {
            STRAIGHT -> StraightThumb(isDragging)
            BALL -> ClassicThumb(isDragging)
            MORPHING -> MorphingThumb()
        }
    }
}

object TrackStyle {
    const val WAVY = "Wavy"
    const val STRAIGHT = "Straight"


    @Composable
    fun toTrack(
        style: String,
        isPlaying: Boolean,
        sliderState: SliderState
    ) {
        when(style) {
            WAVY -> WavyTrack(isPlaying, sliderState)
            STRAIGHT -> StraightTrack(sliderState)
        }
    }

}
