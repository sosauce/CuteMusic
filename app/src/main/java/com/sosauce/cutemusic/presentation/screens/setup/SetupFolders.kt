package com.sosauce.cutemusic.presentation.screens.setup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberMinTrackDuration
import com.sosauce.cutemusic.presentation.screens.settings.compenents.FoldersView
import com.sosauce.cutemusic.presentation.screens.settings.compenents.SliderSettingsCards

@Composable
fun SetupFolders() {

    var minTrackDuration by rememberMinTrackDuration()


    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.initial_folder_selection),
                textAlign = TextAlign.Center
            )
            FoldersView()
            Spacer(Modifier.height(25.dp))
            Text(
                text = stringResource(R.string.min_track_length_hint),
                textAlign = TextAlign.Center
            )
            SliderSettingsCards(
                value = minTrackDuration,
                onValueChange = { minTrackDuration = it },
                topDp = 24.dp,
                bottomDp = 24.dp,
                text = R.string.min_track_length_text
            )
        }
    }
}