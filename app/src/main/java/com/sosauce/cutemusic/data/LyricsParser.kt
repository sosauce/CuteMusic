package com.sosauce.cutemusic.data

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import com.kyant.taglib.TagLib
import com.sosauce.cutemusic.domain.model.Lyrics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException

class LyricsParser(private val context: Context) {

    suspend fun parseLyrics(
        path: String
    ): List<Lyrics> = withContext(Dispatchers.IO) {
        val file = getLrcFile(path)
        val regex = Regex("""\[(\d{2}):(\d{2})\.(\d{2,3})]""")

        return@withContext file?.bufferedReader()?.useLines { lines ->
            lines.mapNotNull { line ->
                regex.find(line)?.let { matchResult ->
                    val (minutes, seconds, hundredths) = matchResult.destructured
                    val millis =
                        minutes.toLong() * 60_000 + seconds.toLong() * 1000 + hundredths.toLong() * 10
                    val lyric = line.substring(matchResult.range.last + 1).trim()
                    Lyrics(millis, lyric)
                }
            }.toList()
        } ?: loadEmbeddedLyrics(path).lineSequence().map { line ->
            regex.find(line)?.let { matchResult ->

                val (minutes, seconds, hundredths) = matchResult.destructured
                val millis =
                    minutes.toLong() * 60_000 + seconds.toLong() * 1000 + hundredths.toLong() * 10
                val lyric = line.substring(matchResult.range.last + 1).trim()
                Lyrics(millis, lyric)
            } ?: Lyrics(0, line)
        }.toList()
    }

    private fun getLrcFile(path: String): File? {
        val lrcFilePath = path.replaceAfterLast('.', "lrc")
        val lrcFile = File(lrcFilePath)
        return if (lrcFile.exists()) lrcFile else null
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

}