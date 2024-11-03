package com.sosauce.cutemusic.ui.screens.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.ui.shared_components.CuteText


@Composable
fun ShareOptionsContent() {
    Column {
        Card(
            onClick = {},
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            shape = RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 24.dp,
                bottomStart = 4.dp,
                bottomEnd = 4.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(
                    start = 10.dp,
                    top = 25.dp,
                    bottom = 25.dp
                ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Android,
                    contentDescription = null
                )
                Spacer(Modifier.width(10.dp))
                CuteText("Android Share Menu")
            }
        }
        Spacer(Modifier.height(4.dp))
        Card(
            onClick = {},
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            shape = RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 4.dp,
                bottomStart = 24.dp,
                bottomEnd = 24.dp
            ),
            modifier = Modifier.fillMaxWidth(),

            ) {
            Row(
                modifier = Modifier.padding(
                    start = 10.dp,
                    top = 25.dp,
                    bottom = 25.dp
                ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ribbon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                )
                Spacer(Modifier.width(10.dp))
                CuteText("CuteShare")
            }
        }
    }
}