@file:Suppress("PrivatePropertyName")

package com.sosauce.cutemusic.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberFollowSys
import com.sosauce.cutemusic.data.datastore.rememberUseAmoledMode
import com.sosauce.cutemusic.data.datastore.rememberUseDarkMode


private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)


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


//@Composable
//fun DefaultOrArtTheme(content: @Composable () -> Unit) {
//
//    val useArtTheme by rememberUseArtTheme()
//
//    if (useArtTheme) {
//        ArtTheme(
//            content = content
//        )
//    } else {
//        CuteMusicTheme(
//            content = content
//        )
//    }
//
//}

@Composable
fun CuteMusicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val useDarkMode by rememberUseDarkMode()
    val useAmoledMode by rememberUseAmoledMode()
    val followSys by rememberFollowSys()

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColors
        else -> LightColors
    }

    val colorSchemeToUse = when {
        useAmoledMode -> colorScheme.copy(
            surface = Color.Black,
            inverseSurface = Color.White,
            background = Color.Black,
        )

        followSys -> colorScheme
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDarkMode) dynamicDarkColorScheme(context) else dynamicLightColorScheme(
                context
            )
        }

        Build.VERSION.SDK_INT < Build.VERSION_CODES.S -> {
            if (useDarkMode) DarkColors else LightColors
        }

        else -> DarkColors
    }

    MaterialTheme(
        colorScheme = colorSchemeToUse,
        typography = Typography(),
        content = content
    )

}

val GlobalFont = FontFamily(Font(R.font.nunito))


//@Composable
//fun ArtTheme(content: @Composable () -> Unit) {
//    val vm = koinViewModel<MusicViewModel>()
//    val context = LocalContext.current
//    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
//    var seedColor by remember { mutableStateOf<Color?>(null) }
//    val useDarkMode by rememberUseDarkMode()
//    val followSys by rememberFollowSys()
//
//    LaunchedEffect(vm.currentArt) {
//        withContext(Dispatchers.IO) {
//            try {
//                val image = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                    context.contentResolver.loadThumbnail(
//                        Uri.parse(vm.currentMusicUri) ?: Uri.EMPTY, Size(96, 96), null
//                    )
//                } else {
//                    Bitmap.createScaledBitmap(
//                        MediaStore.Images.Media.getBitmap(
//                            context.contentResolver,
//                            Uri.parse(vm.currentMusicUri)
//                        ), 10, 10, false
//                    )
//
//                }
//
//                imageBitmap = image.asImageBitmap()
//
//                val generatedColor = calculateSeedColor(image.asImageBitmap())
//
//                seedColor = generatedColor
//            } catch (e: Exception) {
//                e.printStackTrace()
//                seedColor = null
//            }
//        }
//    }
//    val isDark = when {
//        followSys -> isSystemInDarkTheme()
//        useDarkMode -> true
//        else -> true
//    }
//
//    val state = rememberDynamicMaterialThemeState(
//        seedColor = seedColor ?: MaterialTheme.colorScheme.background,
//        isDark = isDark
//    )
//
//
//    DynamicMaterialTheme(
//        animate = false,
//        state = state
//    ) {
//        content()
//    }
//}
//
//private fun calculateSeedColor(bitmap: ImageBitmap): Color {
//    val suitableColors = bitmap.themeColors(fallback = Color.Black)
//    return suitableColors.first()
//}