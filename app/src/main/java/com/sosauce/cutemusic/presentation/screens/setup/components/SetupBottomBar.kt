@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.setup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberHasBeenThroughSetup

@Composable
fun SetupBottomBar(
    hasPermission: Boolean,
    isLastStep: Boolean,
    onGoToNextPage: () -> Unit,
    onNavigateToApp: () -> Unit
) {
    var hasBeenThroughPermission by rememberHasBeenThroughSetup()


    Row(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .navigationBarsPadding()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Next step",
            style = MaterialTheme.typography.bodyLargeEmphasized.copy(
                color = if (!hasPermission) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
            )
        )
        Button(
            onClick = if (isLastStep) {
                {
                    onNavigateToApp()
                    hasBeenThroughPermission = true
                }
            } else {
                onGoToNextPage
            },
            shape = MaterialShapes.Pill.toShape(),
            enabled = hasPermission,
            modifier = Modifier
                .sizeIn(
                    minWidth = 80.dp,
                    minHeight = 80.dp,
                )
        ) {
            val icon = if (isLastStep) {
                R.drawable.check
            } else R.drawable.forward

            Icon(
                painter = painterResource(icon),
                contentDescription = null
            )
        }
    }
}