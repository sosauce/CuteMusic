@file:OptIn(ExperimentalUuidApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.utils

import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.navigation3.runtime.NavKey
import com.kyant.taglib.PropertyMap
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.ui.navigation.Screen
import dev.chrisbanes.haze.HazeEffectScope
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun Modifier.thenIf(
    condition: Boolean,
    modifier: Modifier.() -> Modifier
): Modifier {
    return if (condition) {
        this.then(modifier())
    } else this
}

fun Player.playAtIndex(
    mediaId: String
) {
    val index = (0 until mediaItemCount).indexOfFirst { getMediaItemAt(it).mediaId == mediaId }
    index.takeIf { it != -1 }?.let {
        seekTo(it, 0)
        play()
    }
}

fun Player.playRandom() {
    (0 until mediaItemCount).takeIf { !it.isEmpty() }?.random()?.let { index ->
        seekTo(index, 0)
        play()
    }
}

fun Player.playFromAlbum(
    albumName: String,
    mediaId: String? = null,
    musics: List<MediaItem>
) {
    clearMediaItems()
    musics.filter { music -> music.mediaMetadata.albumTitle.toString() == albumName }
        .sortedWith(
            compareBy(
                { it.mediaMetadata.trackNumber == null || it.mediaMetadata.trackNumber == 0 },
                { it.mediaMetadata.trackNumber }
            ))
        .also { addMediaItems(it) }


    if (mediaId == null) {
        playRandom()
    } else {
        playAtIndex(mediaId)
    }
}

fun Player.playFromArtist(
    artistsName: String,
    mediaId: String? = null,
    musics: List<MediaItem>
) {
    clearMediaItems()
    musics.filter { music -> music.mediaMetadata.artist.toString() == artistsName }
        .also { addMediaItems(it) }

    if (mediaId == null) {
        playRandom()
    } else {
        playAtIndex(mediaId)
    }
}

fun Player.playFromPlaylist(
    playlistSongsId: List<String>,
    mediaId: String? = null,
    musics: List<MediaItem>
) {
    val musics = musics.filter { music -> music.mediaId in playlistSongsId }

    if (musics.isEmpty()) return

    clearMediaItems()

    setMediaItems(musics)


    if (mediaId == null) {
        playRandom()
    } else {
        playAtIndex(mediaId)
    }
}

fun Player.applyLoop(
    shouldLoop: Boolean
) {
    repeatMode = when (shouldLoop) {
        true -> Player.REPEAT_MODE_ONE
        false -> Player.REPEAT_MODE_OFF
    }
}

fun Player.applyShuffle(
    shouldShuffle: Boolean
) {
    shuffleModeEnabled = shouldShuffle
}

fun Player.applyPlaybackSpeed(speed: Float = 1f) {
    playbackParameters = playbackParameters.withSpeed(speed)
}

// Yes Google, a copy & paste of another function REALLY needed an unstable api...
@UnstableApi
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

fun Uri.getBitrate(context: Context): String {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(context, this)
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toInt()?.div(1000)?.toString()?.plus(" kbps") ?: "Unknown"
    } catch (e: Exception) {
        e.stackTrace
        "Error parsing bitrate!"
    } finally {
        retriever.release()
    }
}

fun Long.formatToReadableTime(): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
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

val LazyListState.showCuteSearchbar
    get() =
        if (layoutInfo.totalItemsCount == 0) {
            true
        } else if (
            layoutInfo.visibleItemsInfo.firstOrNull()?.index == 0 &&
            layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
        ) {
            true
        } else {
            layoutInfo.visibleItemsInfo.lastOrNull()?.index != layoutInfo.totalItemsCount - 1
        }

val LazyGridState.showCuteSearchbar
    get() =
        if (layoutInfo.totalItemsCount == 0) {
            true
        } else if (
            layoutInfo.visibleItemsInfo.firstOrNull()?.index == 0 &&
            layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
        ) {
            true
        } else {
            layoutInfo.visibleItemsInfo.lastOrNull()?.index != layoutInfo.totalItemsCount - 1
        }

fun Modifier.ignoreParentPadding(): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(
            constraints.offset(
                30.dp.roundToPx()
            )
        )
        layout(
            placeable.width,
            placeable.height
        ) { placeable.place(0, 0) }
    }

object CurrentScreen {
    var screen by mutableStateOf<NavKey>(Screen.Main)
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

fun <E> MutableSet<E>.addOrRemove(element: E) {
    if (contains(element)) {
        remove(element)
    } else add(element)
}


typealias LastPlayed = Pair<String, Long>

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
fun Modifier.cuteHazeEffect(
    state: HazeState,
    intensity: Dp = 15.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    block: (HazeEffectScope.() -> Unit)? = null,
) = hazeEffect(
    state = state,
    style = HazeStyle(
        backgroundColor = backgroundColor,
        tints = emptyList(),
        blurRadius = intensity,
        noiseFactor = 0f
    ),
    block = block
)

@Composable
fun String.toShape(): Shape = when (this) {
    RoundedCornerShape(5).toString() -> RoundedCornerShape(5)
    MaterialShapes.Square.toString() -> MaterialShapes.Square.toShape()
    MaterialShapes.Cookie9Sided.toString() -> MaterialShapes.Cookie9Sided.toShape()
    MaterialShapes.Cookie12Sided.toString() -> MaterialShapes.Cookie12Sided.toShape()
    MaterialShapes.Clover8Leaf.toString() -> MaterialShapes.Clover8Leaf.toShape()
    MaterialShapes.Arrow.toString() -> MaterialShapes.Arrow.toShape()
    MaterialShapes.Sunny.toString() -> MaterialShapes.Sunny.toShape()
    else -> RoundedCornerShape(5)
}





@Composable
fun rememberInteractionSource(): MutableInteractionSource {
    return remember { MutableInteractionSource() }
}

@Composable
fun rememberAnimatable(initialValue: Float = 0f): Animatable<Float, AnimationVector1D> {
    return remember { Animatable(initialValue) }
}

@Composable
fun rememberFocusRequester(): FocusRequester {
    return remember { FocusRequester() }
}


//@Composable
//fun animateAlignmentAsState(
//    targetAlignment: Alignment,
//): State<Alignment> {
//    val biased = targetAlignment as BiasAlignment
//    val horizontal by animateFloatAsState(biased.horizontalBias, tween(400))
//    val vertical by animateFloatAsState(biased.verticalBias, tween(400))
//    return remember { derivedStateOf { BiasAlignment(horizontal, vertical) } }
//}

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
fun rememberHazeState(): HazeState = remember { HazeState() }

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