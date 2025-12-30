@file:OptIn(ExperimentalUuidApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.util.fastFilter
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.navigation3.runtime.NavKey
import com.kyant.taglib.PropertyMap
import com.sosauce.cutemusic.data.datastore.rememberIsLandscape
import com.sosauce.cutemusic.data.models.Album
import com.sosauce.cutemusic.data.models.Artist
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.data.models.Playlist
import com.sosauce.cutemusic.presentation.navigation.Screen
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


inline fun Modifier.thenIf(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}

fun <T> List<T>.equalsIgnoreOrder(other: List<T>) = this.size == other.size && this.toSet() == other.toSet()

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
    return then(Modifier
        .fillMaxWidth()
        .wrapContentWidth(align))
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
}

fun Player.playOrPause() {
    if (isPlaying) pause() else play()
}

fun Player.changeRepeatMode(
    initialRepeatMode: Int? = null
) {

    if (initialRepeatMode != null) {
        this.repeatMode = initialRepeatMode
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

fun Uri.getBitrate(context: Context): Int {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(context, this)
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toInt()?.div(1000)
            ?: 0
    } catch (e: Exception) {
        e.stackTrace
        0
    } finally {
        retriever.release()
    }
}

fun Long.formatToReadableTime(): String {
    val totalSeconds = this / 1000
    val seconds = totalSeconds % 60
    val totalMinutes = totalSeconds / 60
    val minutes = totalMinutes % 60
    val hours = totalMinutes / 60
    return if (hours > 0)
        String.format(Locale.getDefault(), "%d:%d:%02d", hours, minutes, seconds)
    else String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
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

inline fun <E, K : Comparable<K>> List<E>.ordered(
    sortAsc: Boolean,
    filterSelector: (E) -> Boolean,
    crossinline sortingSelector: (E) -> K?
): List<E> {
    val filtered = this.filter(filterSelector)
    return if (!sortAsc)
        filtered.sortedByDescending(sortingSelector)
    else filtered.sortedBy(sortingSelector)
}

//fun List<MediaItem>.ordered(
//    ascending: Boolean
//): List<MediaItem> {
// return emptyList()
//}


fun List<CuteTrack>.ordered(
    sort: TrackSort,
    ascending: Boolean,
    query: String
): List<CuteTrack> {
    val sortedList = if (ascending) {
        when (sort) {
            TrackSort.TITLE -> sortedBy { it.title }
            TrackSort.ARTIST -> sortedBy { it.artist }
            TrackSort.ALBUM -> sortedBy { it.album }
            TrackSort.YEAR -> sortedBy { it.year }
            TrackSort.DATE_MODIFIED -> sortedBy { it.dateModified }
            TrackSort.AS_ADDED -> this
        }
    } else {
        when (sort) {
            TrackSort.TITLE -> sortedByDescending { it.title }
            TrackSort.ARTIST -> sortedByDescending { it.artist }
            TrackSort.ALBUM -> sortedByDescending { it.album }
            TrackSort.YEAR -> sortedByDescending { it.year }
            TrackSort.DATE_MODIFIED -> sortedByDescending { it.dateModified }
            TrackSort.AS_ADDED -> this.reversed()

        }
    }

    return sortedList.fastFilter { it.title.contains(query, true) }
}

fun List<Album>.ordered(
    sort: AlbumSort,
    ascending: Boolean,
    query: String
): List<Album> {
    val sortedList = if (ascending) {
        when (sort) {
            AlbumSort.NAME -> sortedBy { it.name }
            AlbumSort.ARTIST -> sortedBy { it.artist }
        }
    } else {
        when (sort) {
            AlbumSort.NAME -> sortedByDescending { it.name }
            AlbumSort.ARTIST -> sortedByDescending { it.artist }
        }
    }

    return sortedList.fastFilter { it.name.contains(query, true) }

}

fun List<Artist>.ordered(
    sort: ArtistSort,
    ascending: Boolean,
    query: String
): List<Artist> {
    val sortedList = if (ascending) {
        when (sort) {
            ArtistSort.NAME -> sortedBy { it.name }
            ArtistSort.NB_TRACKS -> sortedBy { it.numberTracks }
            ArtistSort.NB_ALBUMS -> sortedBy { it.numberAlbums }
        }
    } else {
        when (sort) {
            ArtistSort.NAME -> sortedByDescending { it.name }
            ArtistSort.NB_TRACKS -> sortedByDescending { it.numberTracks }
            ArtistSort.NB_ALBUMS -> sortedByDescending { it.numberAlbums }
        }
    }

    return sortedList.fastFilter { it.name.contains(query, true) }

}

fun List<Playlist>.ordered(
    sort: PlaylistSort,
    ascending: Boolean,
    query: String
): List<Playlist> {
    val sortedList = if (ascending) {
        when (sort) {
            PlaylistSort.NAME -> sortedBy { it.name }
            PlaylistSort.NB_TRACKS -> sortedBy { it.musics.size }
            PlaylistSort.TAGS -> sortedBy { it.tags.size }
            PlaylistSort.COLOR -> sortedBy { it.color }
        }
    } else {
        when (sort) {
            PlaylistSort.NAME -> sortedByDescending { it.name }
            PlaylistSort.NB_TRACKS -> sortedByDescending { it.musics.size }
            PlaylistSort.TAGS -> sortedByDescending { it.tags.size }
            PlaylistSort.COLOR -> sortedByDescending { it.color }
        }
    }

    return sortedList.fastFilter { it.name.contains(query, true) }

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
    ArtworkShape.CLASSIC -> RoundedCornerShape(10)
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
    else -> RoundedCornerShape(10)
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