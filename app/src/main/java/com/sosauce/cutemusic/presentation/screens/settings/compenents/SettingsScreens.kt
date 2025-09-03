package com.sosauce.cutemusic.presentation.screens.settings.compenents

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class SettingsScreens() : NavKey {

    @Serializable
    data object Settings : SettingsScreens()

    @Serializable
    data object LookAndFeel : SettingsScreens()

    @Serializable
    data object NowPlaying : SettingsScreens()

    @Serializable
    data object Playback : SettingsScreens()

    @Serializable
    data object Library : SettingsScreens()

}