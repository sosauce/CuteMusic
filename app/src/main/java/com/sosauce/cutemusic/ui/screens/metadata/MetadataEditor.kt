package com.sosauce.cutemusic.ui.screens.metadata

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.shared_components.AppBar
import com.sosauce.cutemusic.ui.theme.GlobalFont
import com.sosauce.cutemusic.utils.ImageUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MetadataEditor(
    music: MediaItem,
    onPopBackStack: () -> Unit,
    onNavigate: (Screen) -> Unit
) {


    MetadataEditorContent(
        music = music,
        onPopBackStack = { onPopBackStack() },
        onNavigate = { onNavigate(it) }
    )

}

@Composable
fun MetadataEditorContent(
    music: MediaItem,
    onPopBackStack: () -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val context = LocalContext.current
    val uri = Uri.parse(music.mediaMetadata.extras?.getString("uri"))

    val editSongLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                Toast.makeText(context, "Song changed identity !" , Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Error: Couldn't help this song escape taxes :(", Toast.LENGTH_SHORT).show()
            }
        }

    var newTitle by remember { mutableStateOf("") }
    var newArtist by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Create a function when im not lazy
                    createEditRequest(
                        uri = uri,
                        context = context,
                        intentSenderLauncher = editSongLauncher
                    )

//                    val contentValues = ContentValues().apply {
//                        put(MediaStore.Audio.Media.TITLE, newTitle)
//                    }
//                    val rowsToUpdate = context.contentResolver.update(uri, contentValues, null, null)
//
//                    if (rowsToUpdate > 0) {
//                        Toast.makeText(context, "Title updated successfully", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(context, "Failed to update title", Toast.LENGTH_SHORT).show()
//                    }


                // End of onClick listener
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null
                )
            }
        },
        topBar = {
            AppBar(
                title = "Edit",
                showBackArrow = true,
                showMenuIcon = false,
                onPopBackStack = { onPopBackStack() },
                onNavigate = { onNavigate(it) }
            )
        }
    ) { value ->
        Column(
            modifier = Modifier
                .padding(value)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = ImageUtils.imageRequester(
                        img = music.mediaMetadata.artworkUri,
                        context = context
                    ),
                    contentDescription = stringResource(id = R.string.artwork),
                    modifier = Modifier
                        .size(300.dp)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(15)),
                    contentScale = ContentScale.Crop
                )
            }

            Column {
                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    label = {
                        Text(
                            text = music.mediaMetadata.title.toString(),
                            fontFamily = GlobalFont
                        )
                    }

                )
            }
        }
    }
}

private fun createEditRequest(
    uri: Uri,
    context: Context,
    intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>,
) {
    val coroutineScope = CoroutineScope(Dispatchers.Main)

    coroutineScope.launch {
        try {
            // How do u edit a file below A11 ??
        } catch (e: SecurityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val intentSender = MediaStore.createWriteRequest(
                    context.contentResolver,
                    listOf(uri)
                ).intentSender

                intentSenderLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
        }
    }
}