@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.kmpalette.color
import com.kmpalette.rememberDominantColorState
import com.materialkolor.DynamicMaterialExpressiveTheme
import com.materialkolor.LocalDynamicMaterialThemeSeed
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicMaterialThemeState
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberAppTheme
import com.sosauce.cutemusic.data.datastore.rememberUseArtTheme
import com.sosauce.cutemusic.utils.CuteTheme
import com.sosauce.cutemusic.utils.anyDarkColorScheme
import com.sosauce.cutemusic.utils.anyLightColorScheme

private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

@Composable
fun CuteMusicTheme(
    artImageBitmap: ImageBitmap,
    content: @Composable () -> Unit,
) {
    val theme by rememberAppTheme()
    val useArtTheme by rememberUseArtTheme()
    val isSystemInDarkTheme = isSystemInDarkTheme()


    val colorSchemeToUse = when (theme) {
        CuteTheme.AMOLED -> anyDarkColorScheme().copy(
            surface = Color.Black,
            inverseSurface = Color.White,
            background = Color.Black,
        )

        CuteTheme.SYSTEM -> if (isSystemInDarkTheme) anyDarkColorScheme() else anyLightColorScheme()
        CuteTheme.DARK -> anyDarkColorScheme()
        CuteTheme.LIGHT -> anyLightColorScheme()
        else -> DarkColors
    }

    val paletteState = rememberDominantColorState()

    LaunchedEffect(artImageBitmap) {
        paletteState.updateFrom(artImageBitmap)
    }

    val state = rememberDynamicMaterialThemeState(
        seedColor = paletteState.result?.paletteOrNull?.vibrantSwatch?.color
            ?: MaterialTheme.colorScheme.primary,
        isDark = if (theme == CuteTheme.SYSTEM) isSystemInDarkTheme else if (theme == CuteTheme.AMOLED) true else theme == CuteTheme.DARK,
        isAmoled = theme == CuteTheme.AMOLED,
        specVersion = ColorSpec.SpecVersion.SPEC_2025
    )

    DynamicMaterialExpressiveTheme(
        state = state,
        motionScheme = MotionScheme.expressive(),
        animate = true,
        content = content
    )

    CompositionLocalProvider(LocalDynamicMaterialThemeSeed provides state.seedColor) {
        MaterialExpressiveTheme(
            //colorScheme = animateColorScheme(if (useArtTheme) state.colorScheme else colorSchemeToUse, tween(500)),
            colorScheme = if (useArtTheme) state.colorScheme else colorSchemeToUse,
            motionScheme = MotionScheme.expressive(),
            content = content,
        )
    }
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
