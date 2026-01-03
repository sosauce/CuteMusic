@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.settings.compenents

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberSliderStyle
import com.sosauce.cutemusic.presentation.screens.playing.components.rememberCuteSliderState
import com.sosauce.cutemusic.presentation.screens.playing.components.toSlider
import com.sosauce.cutemusic.presentation.screens.settings.FontItem
import com.sosauce.cutemusic.presentation.screens.settings.FontStyle
import com.sosauce.cutemusic.presentation.screens.settings.ThemeItem


@Composable
fun SettingsCards(
    modifier: Modifier = Modifier,
    checked: Boolean,
    topDp: Dp,
    bottomDp: Dp,
    text: String,
    onCheckedChange: () -> Unit,
    optionalDescription: Int? = null,
) {
    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        modifier = modifier
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
                    Text(text)
                    optionalDescription?.let {
                        Text(
                            text = stringResource(it),
                            style = MaterialTheme.typography.labelSmallEmphasized.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
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
fun SliderSettingsCards(
    value: Int,
    topDp: Dp,
    bottomDp: Dp,
    text: Int,
    onValueChange: (Int) -> Unit,
    optionalDescription: Int? = null,
) {

    val sliderStyle by rememberSliderStyle()
    var tempValue by remember { mutableStateOf<Int?>(null) }
    val animatedValue by animateIntAsState(
        targetValue = tempValue ?: value
    )


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
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(stringResource(text))
                }
                Text(animatedValue.toString())
            }
            sliderStyle.toSlider(
                rememberCuteSliderState(
                    value = animatedValue.toFloat(),
                    onValueChange = { tempValue = it.toInt() },
                    onValueChangeFinished = {
                        tempValue?.let {
                            onValueChange(it)
                        }
                        tempValue = null
                    },
                    valueRange = 0f..60f
                )
            )
            optionalDescription?.let {
                Text(
                    text = stringResource(it),
                    style = MaterialTheme.typography.labelSmallEmphasized.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
fun ThemeSelector(theme: ThemeItem) {
    val borderColor by animateColorAsState(
        targetValue = if (theme.isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent,
    )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(10.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { theme.onClick() }
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
                .background(theme.backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(theme.iconAndTint.first),
                contentDescription = null,
                tint = theme.iconAndTint.second,
            )
        }
        Spacer(Modifier.weight(1f))
        Text(theme.text)
    }
}

@Composable
fun FontSelector(fontItem: FontItem) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(10.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { fontItem.onClick() }
    ) {
        Box(
            modifier = Modifier
                .padding(10.dp)
                .size(50.dp)
                .clip(MaterialShapes.Cookie9Sided.toShape())
                .border(
                    width = 2.dp,
                    color = fontItem.borderColor,
                    shape = MaterialShapes.Cookie9Sided.toShape()
                )
                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            contentAlignment = Alignment.Center
        ) { fontItem.text() }
        Spacer(Modifier.weight(1f))
        Text(
            text = if (fontItem.fontStyle == FontStyle.SYSTEM) stringResource(R.string.system) else stringResource(
                R.string.default_text
            )
        )
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