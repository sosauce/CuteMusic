package com.sosauce.cutemusic.data

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import com.kyant.taglib.TagLib
import com.mocharealm.accompanist.lyrics.core.model.ISyncedLine
import com.mocharealm.accompanist.lyrics.core.model.SyncedLyrics
import com.mocharealm.accompanist.lyrics.core.model.synced.SyncedLine
import com.mocharealm.accompanist.lyrics.core.parser.AutoParser
import com.mocharealm.accompanist.lyrics.core.parser.EnhancedLrcParser
import com.mocharealm.accompanist.lyrics.core.parser.LrcParser
import com.sosauce.cutemusic.domain.model.Lyrics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException

class LyricsParser(private val context: Context) {

    suspend fun parseLyrics(
        path: String
    ): List<Lyrics> = withContext(Dispatchers.IO) {
        val uri = getLrcFileUri(path)

        return@withContext if (uri != null) {
            context.contentResolver.openInputStream(uri)?.bufferedReader()?.useLines { lines ->
                val lyrics = LrcParser.parse(lines.toList())
                lyrics.lines.fastMap { line -> (line as SyncedLine).toLyricLine() }
            } ?: emptyList()
        } else {

            val autoParser = AutoParser.Builder().build()
            val embeddedLyrics = loadEmbeddedLyrics(path)

            // Tries to load synced embedded lyrics, is embedded lyrics are unsynced, just return raw embedded lyrics

            autoParser.parse(embeddedLyrics)
                .takeIf { it.lines.isNotEmpty() }
                ?.lines?.fastMap { line -> (line as SyncedLine).toLyricLine() } ?: listOf(Lyrics(lineLyrics = embeddedLyrics))
        }

    }

    fun getLrcFileUri(path: String): Uri? {

        val fileName = path.substringAfterLast('/').replaceAfterLast('.', "lrc")

        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Files.getContentUri("external")
        }

        val projection = arrayOf(MediaStore.Files.FileColumns._ID)

        val selection = "${MediaStore.Files.FileColumns.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(fileName)

        return context.contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
                ContentUris.withAppendedId(uri, id)
            } else null
        }
    }

    private fun loadEmbeddedLyrics(path: String): String {
        val fd = getFileDescriptorFromPath(context, path)
        return fd?.dup()?.detachFd()?.let {
            TagLib.getMetadata(it)?.propertyMap?.get("LYRICS")?.getOrNull(0)
                ?: ""
        } ?: ""
    }

    @SuppressLint("Range")
    private fun getFileDescriptorFromPath(
        context: Context,
        filePath: String,
        mode: String = "r"
    ): ParcelFileDescriptor? {
        val resolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(MediaStore.Files.FileColumns._ID)
        val selection = "${MediaStore.Files.FileColumns.DATA}=?"
        val selectionArgs = arrayOf(filePath)

        resolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val fileId = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID))
                if (fileId == -1) {
                    return null
                } else {
                    val fileUri = Uri.withAppendedPath(uri, fileId.toString())
                    try {
                        return resolver.openFileDescriptor(fileUri, mode)
                    } catch (e: FileNotFoundException) {
                        Log.e("MediaStoreReceiver", "File not found: ${e.message}")
                    }
                }
            }
        }

        return null
    }

    private fun SyncedLine.toLyricLine(): Lyrics {
        return Lyrics(
            timestamp = this.start,
            lineLyrics = this.content
        )
    }

}