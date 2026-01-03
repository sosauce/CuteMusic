@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.setup.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.sosauce.cutemusic.R

@Composable
fun SetupHeader(progress: () -> Float) {
    Column(
        modifier = Modifier.statusBarsPadding()
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.displayLargeEmphasized.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 42.sp,
                lineHeight = 1.1.em
            )
        )
        Spacer(Modifier.height(20.dp))
        LinearWavyProgressIndicator(
            progress = progress,
            stopSize = 0.dp,
            modifier = Modifier.fillMaxWidth()
        )
    }

}