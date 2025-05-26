@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.ui.screens.settings.compenents

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.ui.shared_components.CuteText

@Composable
fun SettingsCards(
    checked: Boolean,
    topDp: Dp,
    bottomDp: Dp,
    text: String,
    onCheckedChange: () -> Unit,
    optionalDescription: (@Composable () -> Unit)? = null,
) {
    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier
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
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
            ) {
                Column {
                    CuteText(
                        text = text
                    )
                    optionalDescription?.invoke()
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = { onCheckedChange() },
                colors = SwitchDefaults.colors(
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun SwitchWithText(
    checked: Boolean,
    onCheckedChange: () -> Unit,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(
                horizontal = 15.dp,
                vertical = 10.dp
            )
    ) {
        CuteText(
            text = text,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = { onCheckedChange() },
            colors = SwitchDefaults.colors(
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
fun TextSettingsCards(
    text: String,
    onClick: () -> Unit,
    topDp: Dp,
    bottomDp: Dp,
) {
    Card(
        shape = RoundedCornerShape(
            topStart = topDp,
            topEnd = topDp,
            bottomStart = bottomDp,
            bottomEnd = bottomDp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .clip(
                RoundedCornerShape(
                    topStart = topDp,
                    topEnd = topDp,
                    bottomStart = bottomDp,
                    bottomEnd = bottomDp
                )
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(
                    vertical = 25.dp,
                    horizontal = 15.dp
                )
        ) {
            CuteText(text)
        }
    }
}


@Composable
fun ThemeSelector(
    onClick: () -> Unit,
    backgroundColor: Color,
    icon: @Composable () -> Unit,
    text: String,
    isThemeSelected: Boolean
) {
    val borderColor by animateColorAsState(
        targetValue = if (isThemeSelected) MaterialTheme.colorScheme.secondary else Color.Transparent,
    )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(10.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .size(50.dp)
                .clip(MaterialShapes.Cookie9Sided.toShape())
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = MaterialShapes.Cookie9Sided.toShape()
                )
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Spacer(Modifier.weight(1f))
        CuteText(text)
    }
}

@Composable
fun ShapeSelector(
    onClick: () -> Unit,
    shape: Shape,
    isSelected: Boolean
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent,
    )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(10.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .size(50.dp)
                .clip(shape)
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = shape
                )
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        )
    }
}

@Composable
fun SliderSelector(
    onClick: () -> Unit,
    isSelected: Boolean,
    slider: @Composable () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent,
    )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(10.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .height(50.dp)
                .width(100.dp)
                .clip(RoundedCornerShape(5.dp))
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(5.dp)
                )
                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.padding(5.dp)
            ) {
                slider()
            }
        }
    }
}