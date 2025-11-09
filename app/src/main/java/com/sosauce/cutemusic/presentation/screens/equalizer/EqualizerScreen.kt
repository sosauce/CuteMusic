@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.presentation.screens.equalizer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberEqBands
import com.sosauce.cutemusic.presentation.screens.settings.compenents.SettingsCards
import com.sosauce.cutemusic.presentation.shared_components.CuteActionButton
import com.sosauce.cutemusic.presentation.shared_components.CuteNavigationButton
import com.sosauce.cutemusic.presentation.shared_components.animations.AnimatedSlider

@Composable
fun EqualizerScreen(
    onChangeDecibelLevel: (frequency: Int, level: Float) -> Unit,
    onResetBands: () -> Unit,
    onNavigateUp: () -> Unit
) {

    val eqBands by rememberEqBands()

    Scaffold(
        topBar = {
            SettingsCards(
                modifier = Modifier.statusBarsPadding(),
                checked = true,
                onCheckedChange = {},
                topDp = 24.dp,
                bottomDp = 24.dp,
                text = "Enable equalizer"
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Transparent
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CuteNavigationButton(
                        onNavigateUp = onNavigateUp
                    )
                    CuteActionButton(
                        action = onResetBands,
                        imageVector = ImageVector.vectorResource(R.drawable.reset)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 10.dp)
        ) {
            Spacer(Modifier.height(40.dp))
            eqBands.forEach { (frequencies, decibel) ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("$frequencies Hz")
                    Text("${"%.1f".format(decibel)} dB")
                }

                Spacer(Modifier.height(10.dp))
                AnimatedSlider(
                    value = decibel,
                    onValueChange = {
                        onChangeDecibelLevel(
                            frequencies.substringBefore("-").toInt(),
                            it
                        )
                    },
                    valueRange = -15f..15f
                )
            }
        }

    }

}