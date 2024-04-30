package com.sosauce.cutemusic.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Loop
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.media3.common.Player
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.logic.dataStore
import com.sosauce.cutemusic.logic.getLoopSetting
import com.sosauce.cutemusic.logic.saveLoopSetting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun LoopButton(
    player: Player,
    viewModel: MusicViewModel
) {


    val context = LocalContext.current
    val dataStore: DataStore<Preferences> = context.dataStore
    val coroutineScope = rememberCoroutineScope()
    val loopEnabledFlow: Flow<Boolean> = getLoopSetting(context.dataStore)
    val loopEnabledState: State<Boolean> = loopEnabledFlow.collectAsState(initial = false)



    IconButton(
        onClick = {
            if (loopEnabledState.value) {
                player.repeatMode = Player.REPEAT_MODE_OFF
                coroutineScope.launch {
                    saveLoopSetting(dataStore, false)
                }
            } else {
                player.repeatMode = Player.REPEAT_MODE_ONE
                coroutineScope.launch {
                    saveLoopSetting(dataStore, true)
                }
            }
        }
    ) {
        Icon(
            imageVector = Icons.Outlined.Loop,
            contentDescription = "loop button",
            tint = viewModel.iconTint()
        )
    }
}

@Composable
fun ShuffleButton(
    player: Player,
    viewModel: MusicViewModel
) {
    IconButton(
        onClick = { player.shuffleModeEnabled = !player.shuffleModeEnabled }
    ) {
        Icon(
            imageVector = Icons.Outlined.Shuffle,
            contentDescription = "shuffle button",
            tint = viewModel.shuffleIconTint()
        )
    }
}