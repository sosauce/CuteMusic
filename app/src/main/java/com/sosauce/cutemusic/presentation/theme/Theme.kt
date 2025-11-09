@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.kmpalette.color
import com.kmpalette.rememberDominantColorState
import com.materialkolor.DynamicMaterialExpressiveTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicMaterialThemeState
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberAppTheme
import com.sosauce.cutemusic.data.datastore.rememberUseArtTheme
import com.sosauce.cutemusic.data.datastore.rememberUseExpressivePalette
import com.sosauce.cutemusic.data.datastore.rememberUseSystemFont
import com.sosauce.cutemusic.utils.CuteTheme
import com.sosauce.cutemusic.utils.anyDarkColorScheme

@Composable
fun CuteMusicTheme(
    artImageBitmap: ImageBitmap?,
    content: @Composable () -> Unit,
) {
    val theme by rememberAppTheme()
    val useArtTheme by rememberUseArtTheme()
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val paletteState = rememberDominantColorState()
    val useExpressivePalette by rememberUseExpressivePalette()
    val useSystemFont by rememberUseSystemFont()
    val seedColor = if (useArtTheme && artImageBitmap != null) {
        paletteState.result?.paletteOrNull?.vibrantSwatch?.color
            ?: MaterialTheme.colorScheme.primary
    } else {
        anyDarkColorScheme().primary
    }

    LaunchedEffect(artImageBitmap) {
        if (artImageBitmap != null) {
            paletteState.updateFrom(artImageBitmap)
        }
    }

    val state = rememberDynamicMaterialThemeState(
        seedColor = seedColor,
        isDark = if (theme == CuteTheme.SYSTEM) isSystemInDarkTheme else if (theme == CuteTheme.AMOLED) true else theme == CuteTheme.DARK,
        isAmoled = theme == CuteTheme.AMOLED,
        specVersion = ColorSpec.SpecVersion.SPEC_2025,
        style = if (useExpressivePalette) PaletteStyle.Expressive else PaletteStyle.Fidelity
    )

    DynamicMaterialExpressiveTheme(
        state = state,
        motionScheme = MotionScheme.expressive(),
        animate = false,
        typography = if (useSystemFont) MaterialTheme.typography else NunitoTypography,
        content = content
    )
}

val nunitoFontFamily = FontFamily(
    Font(R.font.nunito_black, FontWeight.Black, FontStyle.Normal),
    Font(R.font.nunito_bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.nunito_extrabold, FontWeight.ExtraBold, FontStyle.Normal),
    Font(R.font.nunito_light, FontWeight.Light, FontStyle.Normal),
    Font(R.font.nunito_medium, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.nunito_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.nunito_semibold, FontWeight.SemiBold, FontStyle.Normal),
)

val NunitoTypography = Typography().run {
    copy(
        displayLarge = displayLarge.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        displayMedium = displayMedium.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        displaySmall = displaySmall.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        headlineLarge = headlineLarge.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        headlineMedium = headlineMedium.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        headlineSmall = headlineSmall.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        titleLarge = titleLarge.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        titleMedium = titleMedium.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        titleSmall = titleSmall.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        bodyLarge = bodyLarge.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        bodyMedium = bodyMedium.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        bodySmall = bodySmall.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        labelLarge = labelLarge.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        labelMedium = labelMedium.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        labelSmall = labelSmall.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        displayLargeEmphasized = displayLargeEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        displayMediumEmphasized = displayMediumEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        displaySmallEmphasized = displaySmallEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        headlineLargeEmphasized = headlineLargeEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        headlineMediumEmphasized = headlineMediumEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        headlineSmallEmphasized = headlineSmallEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        titleLargeEmphasized = titleLargeEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        titleMediumEmphasized = titleMediumEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        titleSmallEmphasized = titleSmallEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        bodyLargeEmphasized = bodyLargeEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        bodyMediumEmphasized = bodyMediumEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        bodySmallEmphasized = bodySmallEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        labelLargeEmphasized = labelLargeEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        labelMediumEmphasized = labelMediumEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        ),
        labelSmallEmphasized = labelSmallEmphasized.copy(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.ExtraBold
        )
    )
}


