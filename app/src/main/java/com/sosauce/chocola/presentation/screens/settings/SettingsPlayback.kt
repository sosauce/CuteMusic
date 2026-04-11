@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.data.datastore.rememberPauseOnMute
import com.sosauce.chocola.data.datastore.rememberSeekButtonsDuration
import com.sosauce.chocola.presentation.screens.settings.compenents.SettingsSwitch
import com.sosauce.chocola.presentation.screens.settings.compenents.SettingsWithTitle
import com.sosauce.chocola.presentation.screens.settings.compenents.SliderSettingsCards

@Composable
fun SettingsPlayback() {

    val scrollState = rememberScrollState()
    var pauseOnMute by rememberPauseOnMute()
    var seekButtonsDuration by rememberSeekButtonsDuration()


    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
    ) {

        SettingsWithTitle(
            title = R.string.playback
        ) {
            SliderSettingsCards(
                value = seekButtonsDuration,
                onValueChange = { seekButtonsDuration = it },
                topDp = 24.dp,
                bottomDp = 24.dp,
                text = R.string.seek_buttons_duration
            )
        }

        SettingsWithTitle(
            title = R.string.audio
        ) {
            SettingsSwitch(
                checked = pauseOnMute,
                onCheckedChange = { pauseOnMute = !pauseOnMute },
                topDp = 24.dp,
                bottomDp = 24.dp,
                text = stringResource(R.string.pause_on_mute),
                optionalDescription = R.string.pause_on_mute_desc
            )
        }
    }

}