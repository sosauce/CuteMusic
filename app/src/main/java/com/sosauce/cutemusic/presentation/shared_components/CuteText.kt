package com.sosauce.cutemusic.presentation.shared_components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.sosauce.cutemusic.data.datastore.rememberUseSystemFont
import com.sosauce.cutemusic.presentation.theme.nunitoFontFamily

@Composable
fun CuteText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = FontWeight.ExtraBold,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = LocalTextStyle.current,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val useSystemFont by rememberUseSystemFont()
    val fontFamily = if (useSystemFont) {
        null
    } else {
        nunitoFontFamily
    }

    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        textAlign = textAlign,
        maxLines = maxLines,
        fontFamily = fontFamily,
        style = style,
        onTextLayout = onTextLayout,
        overflow = overflow
    )
}