package com.sosauce.cutemusic.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sosauce.cutemusic.ui.theme.GlobalFont

@Composable
fun SettingsCards(
    hasInfoDialog: Boolean = false,
    checked: Boolean,
    onCheckedChange: () -> Unit,
    onClick: (() -> Unit)? = null,
    topDp: Dp,
    bottomDp: Dp,
    text: String
) {
    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        shape = RoundedCornerShape(
            topStart = topDp,
            topEnd = topDp,
            bottomStart = bottomDp,
            bottomEnd = bottomDp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = text,
                    fontFamily = GlobalFont
                )
                if (hasInfoDialog) {
                    IconButton(
                        onClick = { onClick?.invoke() }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Info Button"
                        )
                    }
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = { onCheckedChange() }
            )
        }
    }
}

@Composable
fun TextSettingsCards(
    text: String,
    tipText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(
            topStart = 4.dp,
            topEnd = 4.dp,
            bottomStart = 24.dp,
            bottomEnd = 24.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth()
        ) {
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = text,
                    fontFamily = GlobalFont
                )
                Text(
                    text = tipText,
                    fontFamily = GlobalFont,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}