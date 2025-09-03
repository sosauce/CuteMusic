@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.shared_components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun NoXFound(
    headlineText: Int,
    bodyText: Int,
    icon: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(70.dp)
        )
        Spacer(Modifier.height(10.dp))
        CuteText(
            text = stringResource(headlineText),
            style = MaterialTheme.typography.headlineMediumEmphasized,
            fontWeight = FontWeight.Black
        )
        CuteText(
            text = stringResource(bodyText),
            style = MaterialTheme.typography.bodyMediumEmphasized,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}