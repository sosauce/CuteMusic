package com.sosauce.cutemusic.data.models

import android.net.Uri
import androidx.media3.common.MediaItem
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class CuteTrack(
    val mediaId: String = "",
    @Serializable(with = UriSerializer::class)
    val uri: Uri = Uri.EMPTY,
    @Serializable(with = UriSerializer::class)
    val artUri: Uri = Uri.EMPTY,
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val albumId: Long = 0,
    val artistId: Long = 0,
    val durationMs: Long = 0,
    val trackNumber: Int = 0,
    val year: Int = 0,
    val size: Long = 0,
    val folder: String = "",
    val path: String = "",
    val isSaf: Boolean = false,
    val dateModified: Long = 0,
    @Transient
    val mediaItem: MediaItem = MediaItem.EMPTY
)

object UriSerializer : KSerializer<Uri> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Uri", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: Uri
    ) = encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): Uri = Uri.parse(decoder.decodeString())
}



