package com.sosauce.cutemusic.main

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sosauce.cutemusic.ui.navigation.Nav
import com.sosauce.cutemusic.ui.theme.CuteMusicTheme

class MainActivity : ComponentActivity() {

    private val app by lazy { application as App }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            //if (it) {  }
        }


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        setContent {
            CuteMusicTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    val context = LocalContext.current
                    MaterialTheme(
                        content = {
                            Nav(app = app)
                            //MusicScannerDialog(context)
                            requestPermission()
                        }
                    )
                }
            }
        }
    }

    
    private fun requestPermission() {
        requestPermissionLauncher.launch(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        )
    }
}

//@Composable
//fun MusicScannerDialog(context: Context) {
//    val coroutineScope = rememberCoroutineScope()
//    val showDialog = remember { mutableStateOf(false) }
//    val scanDuration = remember { mutableLongStateOf(0L) }
//    val statistics = remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
//
//    LaunchedEffect(Unit) {
//        coroutineScope.launch {
//            val (musicList, duration, stats) = getMusicsTest(context)
//            scanDuration.longValue = duration
//            statistics.value = stats
//            showDialog.value = true
//
//        }
//    }
//
//    if (showDialog.value) {
//        AlertDialog(
//            onDismissRequest = { showDialog.value = false },
//            title = { Text("Completed") },
//            text = {
//                Text(
//                    "Full rescan fetched ${statistics.value["songs"]} songs, " +
//                            "${statistics.value["albums"]} albums, " +
//                            "${statistics.value["artists"]} artists, " +
//                            "0 playlists, ${statistics.value["folders"]} folders.\n\n" +
//                            "Took ${scanDuration.longValue} milliseconds."
//                )
//            },
//            confirmButton = {
//                TextButton(onClick = { showDialog.value = false }) {
//                    Text("Confirm")
//                }
//            }
//        )
//    }
//}
//
//
//fun getMusicsTest(context: Context): Triple<List<MediaItem>, Long, Map<String, Int>> {
//    val startTime = System.currentTimeMillis()
//
//    val musics = mutableListOf<MediaItem>()
//    val albums = mutableSetOf<Long>()
//    val artists = mutableSetOf<String>()
//    val folders = mutableSetOf<String>()
//
//    val projection = arrayOf(
//        MediaStore.Audio.Media._ID,
//        MediaStore.Audio.Media.TITLE,
//        MediaStore.Audio.Media.ARTIST,
//        MediaStore.Audio.Media.ALBUM_ID,
//        MediaStore.Audio.Media.DATA,
//        MediaStore.Audio.Media.SIZE,
//        MediaStore.Audio.Media.MIME_TYPE,
//    )
//
//    context.contentResolver.query(
//        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//        projection,
//        null,
//        null,
//        null
//    )?.use { cursor ->
//        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
//        val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
//        val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
//        val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
//        val folderColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
//        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
//        val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
//
//        while (cursor.moveToNext()) {
//            val id = cursor.getLong(idColumn)
//            val title = cursor.getString(titleColumn)
//            val artist = cursor.getString(artistColumn)
//            val albumId = cursor.getLong(albumIdColumn)
//            val filePath = cursor.getString(folderColumn)
//            val folder = filePath.substring(0, filePath.lastIndexOf('/'))
//            val size = cursor.getLong(sizeColumn)
//            val mimeType = cursor.getString(mimeTypeColumn)
//            val uri = ContentUris.withAppendedId(
//                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                id
//            )
//            val artUri = ContentUris.appendId(
//                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.buildUpon(), id)
//                .appendPath("albumart").build()
//
//            albums.add(albumId)
//            artists.add(artist)
//            folders.add(folder)
//
//            musics.add(
//                MediaItem
//                    .Builder()
//                    .setUri(uri)
//                    .setMediaId(id.toString())
//                    .setMimeType(mimeType)
//                    .setMediaMetadata(
//                        MediaMetadata
//                            .Builder()
//                            .setIsBrowsable(false)
//                            .setIsPlayable(true)
//                            .setTitle(title)
//                            .setArtist(artist)
//                            .setArtworkUri(artUri)
//                            .setExtras(Bundle().apply {
//                                putLong("albumId", albumId)
//                                putString("folder", folder)
//                                putLong("size", size)
//                                putString("uri", uri.toString())
//                            }).build()
//                    ).build()
//            )
//        }
//    }
//
//    val endTime = System.currentTimeMillis()
//    val scanDuration = endTime - startTime
//
//    val statistics = mapOf(
//        "songs" to musics.size,
//        "albums" to albums.size,
//        "artists" to artists.size,
//        "folders" to folders.size
//    )
//
//    return Triple(musics, scanDuration, statistics)
//}

