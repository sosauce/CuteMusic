@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberPauseOnMute
import com.sosauce.cutemusic.presentation.screens.settings.compenents.SettingsCards
import com.sosauce.cutemusic.presentation.screens.settings.compenents.SettingsWithTitle
import com.sosauce.cutemusic.presentation.shared_components.CuteNavigationButton

@Composable
fun SettingsPlayback(
    onNavigateUp: () -> Unit
) {

    val scrollState = rememberScrollState()
    var pauseOnMute by rememberPauseOnMute()


    Scaffold(
        bottomBar = {
            CuteNavigationButton(onNavigateUp = onNavigateUp)
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(pv)
        ) {
            SettingsWithTitle(
                title = R.string.audio
            ) {
                SettingsCards(
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

}