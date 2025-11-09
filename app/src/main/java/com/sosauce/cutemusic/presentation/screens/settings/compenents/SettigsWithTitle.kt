package com.sosauce.cutemusic.presentation.screens.settings.compenents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp


@Composable
fun SettingsWithTitle(
    title: Int,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = stringResource(id = title),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 34.dp, vertical = 8.dp)
        )
        content()
    }
}