package com.sosauce.chocola.presentation.screens.settings

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.chocola.data.datastore.UserPreferences
import com.sosauce.chocola.data.models.EqualizerBand
import com.sosauce.chocola.data.models.EqualizerPreset
import com.sosauce.chocola.utils.EQUALIZER_ACTION_BROADCAST
import com.sosauce.chocola.utils.PACKAGE
import com.sosauce.chocola.utils.WIDGET_ACTION_BROADCAST
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class PlaybackSettingsViewModel(
    private val application: Application,
    private val userPreferences: UserPreferences
): AndroidViewModel(application) {

    private val _state = MutableStateFlow(PlaybackSettingsState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferences.getEqualizerBandsFlow().collectLatest { eqBands ->
                _state.update {
                    it.copy(
                        eqBands = eqBands
                    )
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            _state.update {
                it.copy(eqPresets = userPreferences.getEqualizerPresets())
            }
        }
    }

    fun handlePlaybackSettingsActions(action: PlaybackSettingsActions) {
        when(action) {
            is PlaybackSettingsActions.ToggleEqualizer -> {
                val bundle = Bundle().apply {
                    putBoolean("enable", action.enable)
                }
                createEqualizerPendingIntent(EqualizerAction.TOGGLE, bundle).send()
            }
            is PlaybackSettingsActions.SetBandGain -> {
                val bundle = Bundle().apply {
                    putInt("centerFrequency", action.centerFrequencyMilliHertz)
                    putShort("gainMilliBel", action.gainMilliBel)
                }
                createEqualizerPendingIntent(EqualizerAction.SET_NEW_GAIN, bundle).send()
            }
            is PlaybackSettingsActions.UsePreset -> {
                val bundle = Bundle().apply { putShort("presetBand", action.presetBand) }
                createEqualizerPendingIntent(EqualizerAction.USE_PRESET, bundle).send()
            }
        }
    }

    private fun createEqualizerPendingIntent(
        equalizerAction: String,
        bundle: Bundle
    ): PendingIntent {
        val pendingIntent = PendingIntent.getBroadcast(
            application,
            Random.nextInt(),
            Intent(PACKAGE).apply {
                putExtras(bundle)
                putExtra(
                    EQUALIZER_ACTION_BROADCAST,
                    equalizerAction
                )
            },
            PendingIntent.FLAG_IMMUTABLE

        )

        return pendingIntent
    }

}

data class PlaybackSettingsState(
    val eqBands: List<EqualizerBand> = emptyList(),
    val eqPresets: List<EqualizerPreset> = emptyList()
)

sealed interface PlaybackSettingsActions {
    data class ToggleEqualizer(val enable: Boolean): PlaybackSettingsActions
    data class UsePreset(val presetBand: Short): PlaybackSettingsActions
    data class SetBandGain(
        val centerFrequencyMilliHertz: Int,
        val gainMilliBel: Short
    ): PlaybackSettingsActions
}

interface EqualizerCallback {
    fun toggle(enable: Boolean)
    fun setBandGain(centerFrequencyMilliHertz: Int, gainMilliBel: Short)
    fun usePreset(presetBand: Short)
}

object EqualizerAction {
    const val SET_NEW_GAIN = "SET_NEW_GAIN"
    const val TOGGLE = "TOGGLE"
    const val USE_PRESET = "USE_PRESET"
}