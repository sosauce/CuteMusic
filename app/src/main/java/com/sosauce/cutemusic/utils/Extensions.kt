@file:OptIn(ExperimentalUuidApi::class)

package com.sosauce.cutemusic.utils

import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.navigation.NavHostController
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

fun Long.formatBinarySize(): String {
    val kiloByteAsByte = 1.0 * 1024.0
    val megaByteAsByte = 1.0 * 1024.0 * 1024.0
    val gigaByteAsByte = 1.0 * 1024.0 * 1024.0 * 1024.0
    return when {
        this < kiloByteAsByte -> "${this.toDouble()} B"
        this >= kiloByteAsByte && this < megaByteAsByte -> "${
            String.format(
                Locale.getDefault(),
                "%.2f",
                (this / kiloByteAsByte)
            )
        } KB"

        this >= megaByteAsByte && this < gigaByteAsByte -> "${
            String.format(
                Locale.getDefault(),
                "%.2f",
                (this / megaByteAsByte)
            )
        } MB"

        else -> "Too Big!"
    }
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
    val range = 0..mediaItemCount
    seekTo(range.random(), 0)
    play()
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

fun Player.applyPlaybackSpeed(
    speed: Float = 1f,
    pitch: Float = 1f,
) {
    playbackParameters = PlaybackParameters(
        speed,
        pitch
    )
}


fun ByteArray.getUriFromByteArray(context: Context): Uri {
    val albumArtFile = File(context.cacheDir, "albumArt_${this.hashCode()}_${Uuid.random()}.jpg")
    try {
        FileOutputStream(albumArtFile).use { os ->
            os.write(this)
        }
        return Uri.fromFile(albumArtFile)
    } catch (e: Exception) {
        return Uri.EMPTY
    }
}

fun Uri.getBitrate(context: Context): String {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(context, this)
        val bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
        bitrate?.toInt()?.div(1000)?.toString()?.plus(" kbps") ?: "Unknown"
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

object CurrentScreen {
    var screen by mutableStateOf(Screen.Main.toString())
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

fun <T : Any> NavHostController.navigateSingleTop(route: T) =
    navigate(route) { launchSingleTop = true }

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
fun animateAlignmentAsState(
    targetAlignment: Alignment,
): State<Alignment> {
    val biased = targetAlignment as BiasAlignment
    val horizontal by animateFloatAsState(biased.horizontalBias, tween(400))
    val vertical by animateFloatAsState(biased.verticalBias, tween(400))
    return remember { derivedStateOf { BiasAlignment(horizontal, vertical) } }
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