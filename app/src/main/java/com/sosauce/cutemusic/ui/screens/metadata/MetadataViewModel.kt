package com.sosauce.cutemusic.ui.screens.metadata

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kyant.taglib.AudioProperties
import com.kyant.taglib.AudioPropertiesReadStyle
import com.kyant.taglib.Metadata
import com.kyant.taglib.Picture
import com.kyant.taglib.TagLib
import com.sosauce.cutemusic.data.actions.MetadataActions
import com.sosauce.cutemusic.utils.toAudioFileMetadata
import com.sosauce.cutemusic.utils.toModifiableMap
import com.sosauce.cutemusic.utils.toPropertyMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

// Inspired by Metadator and TagLib !

class MetadataViewModel(
    private val application: Application
) : AndroidViewModel(application) {

    private val _metadata = MutableStateFlow(MetadataState())
    val metadataState = _metadata.asStateFlow()


    override fun onCleared() {
        super.onCleared()
        _metadata.value.mutablePropertiesMap.clear()
    }

    suspend fun loadMetadata() {
        runCatching {
            getFileDescriptorFromPath(application, metadataState.value.songPath)?.use { fd ->
                val metadata = loadAudioMetadata(fd)
                val audioProperties = loadAudioProperties(fd)
                val audioArt = loadAudioArt(fd)

                _metadata.value = _metadata.value.copy(
                    metadata = metadata,
                    audioProperties = audioProperties,
                    art = audioArt
                )

            }
        }.onSuccess {
            metadataState.value.metadata?.propertyMap?.toModifiableMap()?.forEach {
                metadataState.value.mutablePropertiesMap[it.key] = it.value ?: ""
            }
        }
    }


    private suspend fun loadAudioMetadata(songFd: ParcelFileDescriptor): Metadata? {
        val fd = songFd.dup()?.detachFd() ?: throw NullPointerException()

        return withContext(Dispatchers.IO) {
            TagLib.getMetadata(fd)
        }
    }

    private suspend fun loadAudioProperties(
        songFd: ParcelFileDescriptor,
        readStyle: AudioPropertiesReadStyle = AudioPropertiesReadStyle.Fast
    ): AudioProperties? {
        val fd = songFd.dup()?.detachFd() ?: throw NullPointerException()

        return withContext(Dispatchers.IO) {
            TagLib.getAudioProperties(fd, readStyle)
        }
    }

    private suspend fun loadAudioArt(songFd: ParcelFileDescriptor): Picture? {
        val fd = songFd.dup()?.detachFd() ?: throw NullPointerException()

        return withContext(Dispatchers.IO) {
            TagLib.getFrontCover(fd)
        }
    }


    private fun saveAllChanges() {
        try {
            val fd = getFileDescriptorFromPath(application, metadataState.value.songPath, "w")


            fd?.dup()?.detachFd()?.let {
                TagLib.savePropertyMap(
                    it,
                    metadataState.value.mutablePropertiesMap.toAudioFileMetadata().toPropertyMap()
                )
            }

            fd?.dup()?.detachFd()?.let {
                if (metadataState.value.art != null) {
                    TagLib.savePictures(it, arrayOf(metadataState.value.art!!))
                }
            }

            MediaScannerConnection.scanFile(
                application.applicationContext,
                arrayOf(metadataState.value.songPath),
                null,
                null
            )
        } catch (e: Exception) {
            Log.d("hello", "some error occured")
            e.printStackTrace()
        }
    }

    private fun saveNewAudioArt(uri: Uri) {

        // App will crash if it tries to open an input stream on an empty uri !
        if (uri == Uri.EMPTY) return

        val mimeType = application.contentResolver.getType(uri)
        val byteArray = application.contentResolver.openInputStream(uri)?.use { inputStream ->

            val baos = ByteArrayOutputStream()
            BitmapFactory.decodeStream(inputStream).apply {
                compress(Bitmap.CompressFormat.JPEG, 100, baos)
            }

            baos.toByteArray()
        }


        val picture = Picture(
            data = byteArray ?: byteArrayOf(),
            description = "",
            pictureType = "Front Cover",
            mimeType = mimeType ?: "image/jpeg"
        )

        _metadata.value = _metadata.value.copy(
            art = picture
        )
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
                    _metadata.update {
                        it.copy(
                            songPath = action.path,
                            songUri = action.uri
                        )
                    }
                    loadMetadata()
                }
            }

            is MetadataActions.UpdateAudioArt -> {
                saveNewAudioArt(action.newArtUri)
            }

            is MetadataActions.RemoveArtwork -> {
                _metadata.update {
                    it.copy(art = null)
                }
            }
        }
    }
}



