package com.sosauce.cutemusic.ui.screens.metadata

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.MetadataActions
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.shared_components.AppBar
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.utils.ImageUtils

@Composable
fun MetadataEditor(
    music: MediaItem,
    onPopBackStack: () -> Unit,
    onNavigate: (Screen) -> Unit,
    metadataViewModel: MetadataViewModel,
    onEditMusic: (List<Uri>, ActivityResultLauncher<IntentSenderRequest>) -> Unit
) {

    MetadataEditorContent(
        music = music,
        onPopBackStack = onPopBackStack,
        onNavigate = onNavigate,
        metadataState = metadataViewModel.metadataState,
        onMetadataAction = { metadataViewModel.onHandleMetadataActions(it) },
        //vm = metadataViewModel,
        onEditMusic = onEditMusic
    )
}

@Composable
fun MetadataEditorContent(
    music: MediaItem,
    onPopBackStack: () -> Unit,
    onNavigate: (Screen) -> Unit,
    metadataState: MetadataState,
    onMetadataAction: (MetadataActions) -> Unit,
    onEditMusic: (List<Uri>, ActivityResultLauncher<IntentSenderRequest>) -> Unit
    //vm: MetadataViewModel
) {
    val context = LocalContext.current
    val uri = Uri.parse(music.mediaMetadata.extras?.getString("uri"))
    val path = music.mediaMetadata.extras?.getString("path")
//    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
//
//    val photoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
//        selectedImageUri = it
//        vm.changeImage(getFilePathFromUri(context, selectedImageUri!!))
//    }

    val editSongLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                onMetadataAction(MetadataActions.SaveChanges(path!!))
                Toast.makeText(
                    context,
                    context.getString(R.string.success),
                    Toast.LENGTH_SHORT
                ).show()
                onPopBackStack()
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.error_saving),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEditMusic(
                        listOf(uri),
                        editSongLauncher
                    )
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
                title = stringResource(R.string.editor),
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
                        .size(200.dp)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(5))
                        .clickable {
//                            photoPickerLauncher.launch(
//                                PickVisualMediaRequest(
//                                    ActivityResultContracts.PickVisualMedia.ImageOnly
//                                ))
                            Toast
                                .makeText(
                                    context,
                                    "Image editing will be available in the future!",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        },
                    contentScale = ContentScale.Crop
                )
            }

            Column {
                EditTextField(
                    value = metadataState.mutablePropertiesMap[0],
                    label = {
                        CuteText(
                            text = stringResource(R.string.title)
                        )
                    }
                ) { title ->
                    metadataState.mutablePropertiesMap[0] = title
                }
                EditTextField(
                    value = metadataState.mutablePropertiesMap[1],
                    label = {
                        CuteText(
                            text = stringResource(R.string.artists).removeSuffix("s") // I'm too lazy to do plurals
                        )
                    }
                ) { artist ->
                    metadataState.mutablePropertiesMap[1] = artist
                }
                EditTextField(
                    value = metadataState.mutablePropertiesMap[2],
                    label = {
                        CuteText(
                            text = stringResource(R.string.albums).removeSuffix("s") // I'm too lazy to do plurals
                        )
                    }
                ) { album ->
                    metadataState.mutablePropertiesMap[2] = album
                }
                Spacer(Modifier.height(25.dp))

                Row {
                    EditTextField(
                        value = metadataState.mutablePropertiesMap[3],
                        label = {
                            CuteText(
                                text = stringResource(R.string.year)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number
                    ) { year ->
                        metadataState.mutablePropertiesMap[3] = year
                    }
                    EditTextField(
                        value = metadataState.mutablePropertiesMap[4],
                        label = {
                            CuteText(
                                text = stringResource(R.string.genre)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) { genre ->
                        metadataState.mutablePropertiesMap[4] = genre
                    }
                }
                Row {
                    EditTextField(
                        value = metadataState.mutablePropertiesMap[5],
                        label = {
                            CuteText(
                                text = stringResource(R.string.track_nb),
                                modifier = Modifier.basicMarquee()
                            )
                        },
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number
                    ) { track ->
                        metadataState.mutablePropertiesMap[5] = track
                    }
                    EditTextField(
                        value = metadataState.mutablePropertiesMap[6],
                        label = {
                            CuteText(
                                text = stringResource(R.string.disc_nb),
                                modifier = Modifier.basicMarquee()
                            )
                        },
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number
                    ) { disc ->
                        metadataState.mutablePropertiesMap[6] = disc
                    }
                }
                EditTextField(
                    value = metadataState.mutablePropertiesMap[7],
                    label = {
                        CuteText(
                            text = "Lyrics",
                            modifier = Modifier.basicMarquee()
                        )
                    }
                ) { lyrics ->
                    metadataState.mutablePropertiesMap[7] = lyrics
                }
            }
        }
    }
}

@Composable
private fun EditTextField(
    modifier: Modifier = Modifier,
    value: String?,
    label: (@Composable () -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Unspecified,
    returnModifiedValue: (String) -> Unit = {}
) {
    OutlinedTextField(
        value = value ?: "",
        onValueChange = { returnModifiedValue(it) },
        label = label,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = keyboardType
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp)
    )
}

