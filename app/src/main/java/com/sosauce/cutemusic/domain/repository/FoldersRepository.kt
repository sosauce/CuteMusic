package com.sosauce.cutemusic.domain.repository

import android.content.Context
import android.provider.MediaStore
import com.sosauce.cutemusic.data.models.Folder


class FoldersRepository(
    private val context: Context
) {


    // Only gets folder with musics in them
    fun fetchFoldersWithMusics(): List<Folder> {

        val folders = mutableListOf<Folder>()

        val projection = arrayOf(MediaStore.Audio.Media.DATA)

        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use {
            val folderPaths = mutableSetOf<String>()
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (it.moveToNext()) {
                val filePath = it.getString(dataColumn)
                val folderPath = filePath.substringBeforeLast('/')
                folderPaths.add(folderPath)
            }
            folderPaths.forEach { path ->
                val folderName = path.substringAfterLast('/')
                folders.add(
                    Folder(
                        name = folderName,
                        path = path,
                    )
                )
            }

        }
        return folders
    }
}