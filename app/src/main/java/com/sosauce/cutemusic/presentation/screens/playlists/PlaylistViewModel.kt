package com.sosauce.cutemusic.presentation.screens.playlists

import android.app.Application
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.models.Playlist
import com.sosauce.cutemusic.data.playlist.PlaylistDao
import com.sosauce.cutemusic.domain.actions.PlaylistActions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistViewModel(
    private val application: Application,
    private val dao: PlaylistDao
) : AndroidViewModel(application) {


    val allPlaylists = dao.getPlaylists()
        .stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5000),
            emptyList()
        )


    fun handlePlaylistActions(action: PlaylistActions) {
        when (action) {
            is PlaylistActions.DeletePlaylist -> {
                viewModelScope.launch(Dispatchers.IO) {
                    dao.deletePlaylist(action.playlist)
                }
            }

            is PlaylistActions.CreatePlaylist -> {
                viewModelScope.launch(Dispatchers.IO) {
                    dao.upsertPlaylist(action.playlist)
                }
            }

            is PlaylistActions.UpsertPlaylist -> {
                viewModelScope.launch(Dispatchers.IO) {
                    dao.upsertPlaylist(action.playlist)
                }
            }

            is PlaylistActions.ImportM3uPlaylist -> {

                val tracksFromFile = mutableListOf<String>()

                viewModelScope.launch(Dispatchers.IO) {
                    ensureActive()
                    application.contentResolver.openInputStream(action.uri)?.bufferedReader()
                        ?.useLines { lines ->
                            for (line in lines) {
                                if (!line.startsWith('/')) continue
                                tracksFromFile.add(getMediaIdFromFilePath(line))
                            }

                        }

                    val playlist = Playlist(
                        emoji = "",
                        name = action.uri.path?.substringAfterLast('/')?.substringBeforeLast('.')
                            ?: "Imported playlist",
                        musics = tracksFromFile,
                        color = -1,
                        tags = emptyList()
                    )

                    dao.upsertPlaylist(playlist)
                }
            }

            is PlaylistActions.ExportM3uPlaylist -> {
                viewModelScope.launch(Dispatchers.IO) {
                    ensureActive()
                    application.contentResolver.openOutputStream(action.uri)?.bufferedWriter()
                        ?.use { writer ->
                            action.tracks.fastForEach { track ->
                                writer.write("${getFilePathFromMediaId(track)}\n")
                            }
                        }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            application,
                            application.getString(R.string.m3u_saved_good),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun getMediaIdFromFilePath(filePath: String): String {
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA
        )

        val selection = "${MediaStore.Audio.Media.DATA} = ?"
        val selectionArgs = arrayOf(filePath)

        application.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val id = cursor.getLong(idColumn)
                return id.toString()
            }
        }

        return ""
    }

    private fun getFilePathFromMediaId(mediaId: String): String {
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA
        )

        val selection = "${MediaStore.Audio.Media._ID} = ?"
        val selectionArgs = arrayOf(mediaId)

        application.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val path = cursor.getString(pathColumn)
                return path
            }
        }

        return ""
    }

}