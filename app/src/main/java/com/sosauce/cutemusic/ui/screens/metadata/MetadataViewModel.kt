package com.sosauce.cutemusic.ui.screens.metadata

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.cutemusic.data.actions.MetadataActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.FileNotFoundException

class MetadataViewModel(
    private val application: Application
) : AndroidViewModel(application) {

    private val _metadata = MutableStateFlow(MetadataState())
    val metadataState = _metadata.asStateFlow()


    override fun onCleared() {
        super.onCleared()
        _metadata.value.mutablePropertiesMap.clear()
    }

    private fun loadMetadataJAudio(path: String) {

//        val audioFile = AudioFileIO
//            .read(File(path))
//
//        audioFile.tag.apply {
//            val tagList = listOf(
//                getFirst(FieldKey.TITLE),
//                getFirst(FieldKey.ARTIST),
//                getFirst(FieldKey.ALBUM),
//                getFirst(FieldKey.YEAR),
//                getFirst(FieldKey.GENRE),
//                getFirst(FieldKey.TRACK),
//                getFirst(FieldKey.DISC_NO),
//                getFirst(FieldKey.LYRICS),
//            )
//
//
//            tagList.forEach {
//                _metadata.value.mutablePropertiesMap.add(it)
//            }
//            //_metadata.value.art = firstArtwork ?: null
//        }
    }


    private fun saveAllChanges() {
    }


    private fun clearState() {
        _metadata.value.mutablePropertiesMap.clear()
        //_metadata.value.art
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


    fun onHandleMetadataActions(action: MetadataActions) {
        when (action) {
            is MetadataActions.SaveChanges -> saveAllChanges()

            is MetadataActions.LoadSong -> {
                viewModelScope.launch {
                    _metadata.value = _metadata.value.copy(
                        songPath = action.path,
                        songUri = action.uri
                    )
                    loadMetadataJAudio(metadataState.value.songPath)
                }
            }

            is MetadataActions.ClearState -> {
                clearState()
            }
        }
    }
}



