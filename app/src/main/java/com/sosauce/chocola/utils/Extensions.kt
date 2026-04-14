@file:OptIn(ExperimentalUuidApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.text.format.DateFormat
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.util.fastFilter
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.kyant.taglib.PropertyMap
import com.materialkolor.PaletteStyle
import com.sosauce.chocola.data.datastore.rememberIsLandscape
import com.sosauce.chocola.data.models.Album
import com.sosauce.chocola.data.models.Artist
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.data.models.Playlist
import com.sosauce.chocola.presentation.navigation.Screen
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import sv.lib.squircleshape.CornerSmoothing
import sv.lib.squircleshape.SquircleShape
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.round
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
fun NavKey.showBackButton(): Boolean {
    return this is Screen.AlbumsDetails || this is Screen.ArtistsDetails || this is Screen.PlaylistDetails
}

fun Context.hasMusicPermission(): Boolean {
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}

fun Modifier.selfAlignHorizontally(align: Alignment.Horizontal = Alignment.CenterHorizontally): Modifier {
    return then(
        Modifier
            .fillMaxWidth()
            .wrapContentWidth(align)
    )
}



val MediaItem.path
    get() = mediaMetadata.extras?.getString("path") ?: ""

val MediaItem.uri: Uri
    get() = mediaMetadata.extras?.getString("uri")?.toUri() ?: Uri.EMPTY

val MediaItem.albumId
    get() = mediaMetadata.extras?.getLong("album_id") ?: 0


fun Player.playRandom() {

    if (mediaItemCount == 0) return

    val randomIndex = Random.nextInt(mediaItemCount)
    seekTo(randomIndex, 0)
    play()
    shuffleModeEnabled = true

}

fun Player.playOrPause() {
    if (isPlaying) pause() else play()
}

fun Player.changeRepeatMode(
    initialRepeatMode: Int? = null
) {

    if (initialRepeatMode != null) {
        repeatMode = initialRepeatMode
    } else {

        val repeatMode = when (repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
        this.repeatMode = repeatMode
    }
}

fun Player.applyShuffle(
    initialShuffle: Boolean? = null
) {

    shuffleModeEnabled = initialShuffle ?: !shuffleModeEnabled
}

fun Player.applyPlaybackSpeed(speed: Float = 1f) {
    playbackParameters = playbackParameters.withSpeed(speed)
}

@SuppressLint("UnsafeOptInUsageError")
fun Player.applyPlaybackPitch(pitch: Float = 1f) {
    playbackParameters = playbackParameters.withPitch(pitch)
}


fun ByteArray.getUriFromByteArray(context: Context): Uri {
    val albumArtFile = File(context.cacheDir, "albumArt_${Uuid.random()}.jpg")
    return try {
        FileOutputStream(albumArtFile).use { os ->
            os.write(this)
        }
        Uri.fromFile(albumArtFile)
    } catch (e: Exception) {
        Uri.EMPTY
    }
}


/**
 * @param metadata FOR EXAMPLE [MediaMetadataRetriever.METADATA_KEY_BITRATE]
 */
fun Uri.getTrackMetadata(
    context: Context,
    metadata: Int
): String? {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(context, this)
        retriever.extractMetadata(metadata)
    } catch (e: Exception) {
        e.stackTrace
        null
    } finally {
        retriever.release()
    }
}

fun Long.formatToReadableTime(): String {
    val duration = this.milliseconds
    return duration.toComponents { hours, minutes, seconds, _ ->
        if (hours > 0) {
            String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
        }
    }
}

fun PropertyMap.toModifiableMap(separator: String = ", "): MutableMap<String, String?> {
    return mutableMapOf(
        "TITLE" to this["TITLE"]?.getOrNull(0),
        "ARTIST" to this["ARTIST"]?.joinToString(separator),
        "ALBUM" to this["ALBUM"]?.getOrNull(0),
        "TRACKNUMBER" to this["TRACKNUMBER"]?.getOrNull(0),
        "DISCNUMBER" to this["DISCNUMBER"]?.getOrNull(0),
        "DATE" to this["DATE"]?.getOrNull(0),
        "GENRE" to this["GENRE"]?.joinToString(separator),
        "LYRICS" to this["LYRICS"]?.getOrNull(0),
        "DATE" to this["DATE"]?.getOrNull(0),
    )
}

fun String?.formatForField(separator: String = ","): Array<String> {
    return this?.split(separator)?.map { it.trim() }?.toTypedArray() ?: arrayOf(this ?: "")
}

fun Modifier.ignoreParentPadding(): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(
            constraints.offset(
                15.dp.roundToPx()
            )
        )
        layout(
            placeable.width,
            placeable.height
        ) { placeable.place(0, 0) }
    }

@Stable
data class AudioFileMetadata(
    val title: String?,
    val artist: String?,
    val album: String?,
    val trackNumber: String?,
    val discNumber: String?,
    val date: String?,
    val genre: String?,
    val lyrics: String?
)

fun Map<String, String?>.toAudioFileMetadata(): AudioFileMetadata {
    return AudioFileMetadata(
        title = this["TITLE"],
        artist = this["ARTIST"],
        album = this["ALBUM"],
        trackNumber = this["TRACKNUMBER"],
        discNumber = this["DISCNUMBER"],
        date = this["DATE"],
        genre = this["GENRE"],
        lyrics = this["LYRICS"]
    )
}

fun AudioFileMetadata.toPropertyMap(): PropertyMap {
    return hashMapOf(
        "TITLE" to arrayOf(title ?: ""),
        "ARTIST" to artist.formatForField(),
        "ALBUM" to arrayOf(album ?: ""),
        "TRACKNUMBER" to arrayOf(trackNumber ?: ""),
        "DISCNUMBER" to arrayOf(discNumber ?: ""),
        "DATE" to arrayOf(date ?: ""),
        "GENRE" to genre.formatForField(),
        "LYRICS" to arrayOf(lyrics ?: "")
    )
}

inline fun <E> List<E>.copyMutate(block: MutableList<E>.() -> Unit): List<E> {
    return toMutableList().apply(block)
}

inline fun <E> Set<E>.copyMutate(block: MutableSet<E>.() -> Unit): Set<E> {
    return toMutableSet().apply(block)
}


fun List<CuteTrack>.ordered(
    sort: TrackSort,
    ascending: Boolean,
    query: String
): List<CuteTrack> {

    // Note to self: Having search first makes sorting only sort we want to display, which is more efficient
    val searchedList = this.fastFilter { it.title.contains(query, true) }.toMutableList()

    // In place sorting!!
    if (ascending) {
        when (sort) {
            TrackSort.TITLE -> searchedList.sortBy { it.title }
            TrackSort.ARTIST -> searchedList.sortBy { it.artist }
            TrackSort.ALBUM -> searchedList.sortBy { it.album }
            TrackSort.YEAR -> searchedList.sortBy { it.year }
            TrackSort.DATE_MODIFIED -> searchedList.sortBy { it.dateModified }
            TrackSort.AS_ADDED -> Unit
        }
    } else {
        when (sort) {
            TrackSort.TITLE -> searchedList.sortByDescending { it.title }
            TrackSort.ARTIST -> searchedList.sortByDescending { it.artist }
            TrackSort.ALBUM -> searchedList.sortByDescending { it.album }
            TrackSort.YEAR -> searchedList.sortByDescending { it.year }
            TrackSort.DATE_MODIFIED -> searchedList.sortByDescending { it.dateModified }
            TrackSort.AS_ADDED -> searchedList.reverse()
        }
    }

    return searchedList
}


fun List<Album>.ordered(
    sort: AlbumSort,
    ascending: Boolean,
    query: String
): List<Album> {
    val result = this.fastFilter { it.name.contains(query, true) }.toMutableList()

    if (ascending) {
        when (sort) {
            AlbumSort.NAME -> result.sortBy { it.name }
            AlbumSort.ARTIST -> result.sortBy { it.artist }
        }
    } else {
        when (sort) {
            AlbumSort.NAME -> result.sortByDescending { it.name }
            AlbumSort.ARTIST -> result.sortByDescending { it.artist }
        }
    }
    return result
}

fun List<Artist>.ordered(
    sort: ArtistSort,
    ascending: Boolean,
    query: String
): List<Artist> {
    val result = this.fastFilter { it.name.contains(query, true) }.toMutableList()

    if (ascending) {
        when (sort) {
            ArtistSort.NAME -> result.sortBy { it.name }
            ArtistSort.NB_TRACKS -> result.sortBy { it.numberTracks }
            ArtistSort.NB_ALBUMS -> result.sortBy { it.numberAlbums }
        }
    } else {
        when (sort) {
            ArtistSort.NAME -> result.sortByDescending { it.name }
            ArtistSort.NB_TRACKS -> result.sortByDescending { it.numberTracks }
            ArtistSort.NB_ALBUMS -> result.sortByDescending { it.numberAlbums }
        }
    }
    return result
}

fun List<Playlist>.ordered(
    sort: PlaylistSort,
    ascending: Boolean,
    query: String
): List<Playlist> {
    val result = this.fastFilter { it.name.contains(query, true) }.toMutableList()

    if (ascending) {
        when (sort) {
            PlaylistSort.NAME -> result.sortBy { it.name }
            PlaylistSort.NB_TRACKS -> result.sortBy { it.musics.size }
            PlaylistSort.TAGS -> result.sortBy { it.tags.size }
            PlaylistSort.COLOR -> result.sortBy { it.color }
        }
    } else {
        when (sort) {
            PlaylistSort.NAME -> result.sortByDescending { it.name }
            PlaylistSort.NB_TRACKS -> result.sortByDescending { it.musics.size }
            PlaylistSort.TAGS -> result.sortByDescending { it.tags.size }
            PlaylistSort.COLOR -> result.sortByDescending { it.color }
        }
    }
    return result
}

fun <E> MutableSet<E>.addOrRemove(element: E) {
    if (contains(element)) {
        remove(element)
    } else add(element)
}


fun ContentResolver.observe(uri: Uri) = callbackFlow {
    val observer = object : ContentObserver(null) {
        override fun onChange(selfChange: Boolean) {

            trySend(selfChange)
        }
    }
    registerContentObserver(uri, true, observer)
    trySend(false)
    awaitClose {
        unregisterContentObserver(observer)
    }
}


@Composable
fun String.toShape(): Shape = when (this) {
    ArtworkShape.CLASSIC -> SquircleShape(percent = 30, smoothing = CornerSmoothing.Full)
    ArtworkShape.CIRCLE -> MaterialShapes.Circle.toShape()
    ArtworkShape.COOKIE_4 -> MaterialShapes.Cookie4Sided.toShape()
    ArtworkShape.COOKIE_9 -> MaterialShapes.Cookie9Sided.toShape()
    ArtworkShape.COOKIE_12 -> MaterialShapes.Cookie12Sided.toShape()
    ArtworkShape.CLOVER_8 -> MaterialShapes.Clover8Leaf.toShape()
    ArtworkShape.SUNNY -> MaterialShapes.Sunny.toShape()
    ArtworkShape.ARROW -> MaterialShapes.Arrow.toShape()
    ArtworkShape.DIAMOND -> MaterialShapes.Diamond.toShape()
    ArtworkShape.BUN -> MaterialShapes.Bun.toShape()
    ArtworkShape.HEART -> MaterialShapes.Heart.toShape()
    else -> SquircleShape(percent = 30, smoothing = CornerSmoothing.Full)
}


val Context.appVersion
    get() = packageManager.getPackageInfo(packageName, 0).versionName


@Composable
fun rememberInteractionSource(): MutableInteractionSource {
    return remember { MutableInteractionSource() }
}

@Composable
fun rememberFocusRequester(): FocusRequester {
    return remember { FocusRequester() }
}


@Composable
fun rememberSearchbarAlignment(
): Alignment {

    val isLandscape = rememberIsLandscape()

    return remember(isLandscape) {
        if (isLandscape) {
            Alignment.BottomEnd
        } else {
            Alignment.BottomCenter
        }
    }
}

@Composable
fun rememberSearchbarMaxFloatValue(
): Float {

    val isLandscape = rememberIsLandscape()

    return remember(isLandscape) {
        if (isLandscape) {
            0.4f
        } else {
            0.85f
        }
    }
}

@Composable
fun rememberSearchbarRightPadding(
): Dp {

    val isLandscape = rememberIsLandscape()

    return remember(isLandscape) {
        if (isLandscape) {
            10.dp
        } else {
            0.dp
        }
    }
}


@Composable
fun anyLightColorScheme(): ColorScheme {
    val context = LocalContext.current

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        dynamicLightColorScheme(context)
    } else {
        lightColorScheme()
    }
}

@Composable
fun anyDarkColorScheme(): ColorScheme {
    val context = LocalContext.current

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        dynamicDarkColorScheme(context)
    } else {
        darkColorScheme()
    }
}

fun <T> bouncySpec() = spring<T>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow
)



val navigationBouncySpec = spring<IntOffset>(Spring.DampingRatioLowBouncy, Spring.StiffnessLow)


val barsContentTransform = ContentTransform(
    targetContentEnter = slideInVertically(
        spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    ) { it } + fadeIn(),
    initialContentExit = slideOutVertically(
        spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    ) { it } + fadeOut(),
    sizeTransform = SizeTransform(clip = false) // prevents the content from getting clipped during bounce
)


fun String.toLyricsAlignment(): TextAlign {
    return when(this) {
        LyricsAlignment.START -> TextAlign.Start
        LyricsAlignment.CENTERED -> TextAlign.Center
        LyricsAlignment.END -> TextAlign.End
        else -> TextAlign.Start
    }
}

@Composable
fun MenuDefaults.getItemShape(index: Int, lastIndex: Int): Shape {
    return when(index) {
        0 -> leadingItemShape
        lastIndex -> trailingItemShape
        else -> middleItemShape
    }
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

fun NavBackStack<NavKey>.navigateBack() {
    // Popping the only screen will crash so this avoids it
    if (size == 1) return
    removeLastOrNull()
}

fun String.toPaletteStyle(): PaletteStyle {
    return when (this) {
        CutePaletteStyle.EXPRESSIVE -> PaletteStyle.Expressive
        CutePaletteStyle.FIDELITY -> PaletteStyle.Fidelity
        CutePaletteStyle.TONAL_SPOT -> PaletteStyle.TonalSpot
        CutePaletteStyle.NEUTRAL -> PaletteStyle.Neutral
        CutePaletteStyle.VIBRANT -> PaletteStyle.Vibrant
        CutePaletteStyle.MONOCHROME -> PaletteStyle.Monochrome
        CutePaletteStyle.FRUIT_SALAD -> PaletteStyle.FruitSalad
        else -> throw IllegalArgumentException("Not a valid palette!")
    }
}

fun Long.formatDate(): String {
    return if (this > 0) {
        val date = java.util.Date(this)
        val formatter = java.text.SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        formatter.format(date)
    } else {
        "Unknown"
    }
}