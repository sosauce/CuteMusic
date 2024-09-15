package com.sosauce.cutemusic.ui.screens.metadata

import android.app.Application
import android.media.MediaScannerConnection
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.cutemusic.data.actions.MetadataActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagOptionSingleton
import java.io.File

class MetadataViewModel(
    private val application: Application
) : AndroidViewModel(application) {

    private val _metadata = MutableStateFlow(MetadataState())
    val metadataState = _metadata.asStateFlow().value

    init {
        TagOptionSingleton.getInstance().isAndroid = true
    }

    override fun onCleared() {
        super.onCleared()
        _metadata.value.mutablePropertiesMap.clear()
    }

    private fun loadMetadataJAudio(path: String) {

        val audioFile = AudioFileIO.read(File(path))

        audioFile.tag.apply {
            val tagList = listOf(
                getFirst(FieldKey.TITLE),
                getFirst(FieldKey.ARTIST),
                getFirst(FieldKey.ALBUM),
                getFirst(FieldKey.YEAR),
                getFirst(FieldKey.GENRE),
                getFirst(FieldKey.TRACK),
                getFirst(FieldKey.DISC_NO),
            )

            tagList.forEach {
                _metadata.value.mutablePropertiesMap.add(it)
            }
            //_metadata.value.art = firstArtwork ?: null
        }
    }


    private fun saveAllChanges(path: String) {
        try {
            val file = File(path)
            val audioFile = AudioFileIO.read(file)
            audioFile.tag.apply {
                val tagList = mapOf(
                    FieldKey.TITLE to 0,
                    FieldKey.ARTIST to 1,
                    FieldKey.ALBUM to 2,
                    FieldKey.YEAR to 3,
                    FieldKey.GENRE to 4,
                    FieldKey.TRACK to 5,
                    FieldKey.DISC_NO to 6
                )
                tagList.forEach {
                    setField(it.key, _metadata.value.mutablePropertiesMap[it.value])
                }
                //setField(ArtworkFactory.)
                AudioFileIO.write(audioFile)
            }
            MediaScannerConnection.scanFile(
                application.applicationContext,
                arrayOf(file.toString()),
                null,
                null
            )

        } catch (e: Exception) {
            Log.d("CuteError", e.message.toString())
        }

    }

    private fun clearState() {
        _metadata.value.mutablePropertiesMap.clear()
        //_metadata.value.art
    }


    fun onHandleMetadataActions(action: MetadataActions) {
        when (action) {
            is MetadataActions.SaveChanges -> {
                viewModelScope.launch {
                    saveAllChanges(action.path)
                }
            }

            is MetadataActions.LoadSong -> {
                viewModelScope.launch {
                    loadMetadataJAudio(action.path)
                }
            }

            is MetadataActions.ClearState -> {
                clearState()
            }
        }
    }

}




