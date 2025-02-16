package com.sosauce.cutemusic.ui.theme

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.palette.graphics.Palette
import coil3.BitmapImage
import coil3.ImageLoader
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.ktx.themeColors
import com.materialkolor.rememberDynamicMaterialThemeState
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberFollowSys
import com.sosauce.cutemusic.data.datastore.rememberUseAmoledMode
import com.sosauce.cutemusic.data.datastore.rememberUseArtTheme
import com.sosauce.cutemusic.data.datastore.rememberUseDarkMode
import com.sosauce.cutemusic.ui.shared_components.MusicViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


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

@Composable
fun CuteMusicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    musicViewModel: MusicViewModel,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val useDarkMode by rememberUseDarkMode()
    val useAmoledMode by rememberUseAmoledMode()
    val followSys by rememberFollowSys()
    val useArtTheme by rememberUseArtTheme()

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

        else -> DarkColors
    }

    if (useArtTheme) {
        val themeProcessingViewModel = viewModel<ThemeProcessingViewModel>()
        val musicState by musicViewModel.musicState.collectAsStateWithLifecycle()

        LaunchedEffect(musicState.currentArt) {
            themeProcessingViewModel.urlToBitmap(musicState.currentArt, context)
        }

        val state = rememberDynamicMaterialThemeState(
            seedColor = Color(
                themeProcessingViewModel.palette?.swatches?.first()?.rgb ?: 0
            ), // I've found this to have the best color accuracy !?
            //seedColor = themeProcessingViewModel.dominantColor,
            isDark = isSystemInDarkTheme() || useDarkMode,
            isAmoled = useAmoledMode
        )

        DynamicMaterialTheme(
            state = state,
            animate = true
        ) { content() }
    } else {
        MaterialTheme(
            colorScheme = colorSchemeToUse,
            typography = Typography(),
            content = content
        )
    }

}

val GlobalFont = FontFamily(Font(R.font.nunito))


class ThemeProcessingViewModel : ViewModel() {


    var palette by mutableStateOf<Palette?>(null)
    var dominantColor by mutableStateOf(Color.Black)

    private fun createPaletteAsync(bitmap: Bitmap) {
        Palette.from(bitmap).generate { generatedPalette ->
            palette = generatedPalette
        }
    }


    fun calculateSeedColor(bitmap: ImageBitmap) {
        val suitableColors = bitmap.themeColors(fallback = Color.Black)
        dominantColor = suitableColors.first()
    }

    fun urlToBitmap(
        imageUri: Uri?,
        context: Context,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imageUri)
                .allowHardware(false) // If set to true it will not work ???
                .build()
            val result = loader.execute(request)
            if (result is SuccessResult) {
                createPaletteAsync((result.image as BitmapImage).bitmap)
                // Note: When MaterialKolor supports Bitmap or is faster use it and remove Palette dependency
                // calculateSeedColor((result.image as BitmapImage).bitmap.asImageBitmap())
            } else if (result is ErrorResult) {
                cancel(result.throwable.localizedMessage ?: "CuteError", result.throwable)
            }
        }
    }


}
