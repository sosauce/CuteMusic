package com.sosauce.cutemusic.ui.screens.metadata

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.actions.MetadataActions
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.shared_components.CuteActionButton
import com.sosauce.cutemusic.ui.shared_components.CuteNavigationButton
import com.sosauce.cutemusic.ui.shared_components.CuteText
import com.sosauce.cutemusic.ui.shared_components.ThreadDivider
import com.sosauce.cutemusic.utils.ImageUtils

@Composable
fun MetadataEditor(
    music: MediaItem,
    onPopBackStack: () -> Unit,
    onNavigate: (Screen) -> Unit,
    metadataViewModel: MetadataViewModel,
    onEditMusic: (List<Uri>, ActivityResultLauncher<IntentSenderRequest>) -> Unit
) {

    val metadataState by metadataViewModel.metadataState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val uri = Uri.parse(music.mediaMetadata.extras?.getString("uri"))
    val photoPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            metadataViewModel.onHandleMetadataActions(
                MetadataActions.UpdateAudioArt(
                    it ?: Uri.EMPTY
                )
            )
        }

    val editSongLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                metadataViewModel.onHandleMetadataActions(MetadataActions.SaveChanges)
                Toast.makeText(
                    context,
                    context.getString(R.string.success),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.error_saving),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = { metadataViewModel.onHandleMetadataActions(MetadataActions.RemoveArtwork) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null
                    )
                }
                if (metadataState.art != null) {
                    AsyncImage(
                        model = ImageUtils.imageRequester(
                            img = metadataState.art?.data,
                            context = context
                        ),
                        contentDescription = stringResource(id = R.string.artwork),
                        modifier = Modifier
                            .size(200.dp)
                            .padding(bottom = 10.dp)
                            .clip(RoundedCornerShape(5))
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .padding(bottom = 10.dp)
                            .clip(RoundedCornerShape(5))
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.add_photo_rounded),
                            contentDescription = stringResource(id = R.string.artwork),
                            modifier = Modifier.size(134.dp), // Size of the parent container divided by 1.5
                            contentScale = ContentScale.Crop,
                            colorFilter = ColorFilter.tint(
                                MaterialTheme.colorScheme.onBackground.copy(
                                    0.9f
                                )
                            )
                        )
                    }
                }
            }



            Column {
                EditTextField(
                    value = metadataState.mutablePropertiesMap["TITLE"],
                    label = {
                        CuteText(
                            text = stringResource(R.string.title)
                        )
                    }
                ) { title ->
                    metadataState.mutablePropertiesMap["TITLE"] = title
                }
                Row(
                    modifier = Modifier.padding(start = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ThreadDivider(
                        color = MaterialTheme.colorScheme.onBackground.copy(0.85f)
                    )
                    CuteText(
                        text = "${stringResource(R.string.file_name)}: ${
                            metadataState.songPath.substringAfterLast(
                                "/"
                            )
                        }",
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 5.dp),
                        color = MaterialTheme.colorScheme.onBackground.copy(0.85f)
                    )
                }

                EditTextField(
                    value = metadataState.mutablePropertiesMap["ARTIST"],
                    verticalPadding = 0.dp,
                    label = {
                        CuteText(
                            text = stringResource(R.string.artists).removeSuffix("s") // I'm too lazy to do plurals
                        )
                    }
                ) { artist ->
                    metadataState.mutablePropertiesMap["ARTIST"] = artist
                }
                EditTextField(
                    value = metadataState.mutablePropertiesMap["ALBUM"],
                    label = {
                        CuteText(
                            text = stringResource(R.string.albums).removeSuffix("s") // I'm too lazy to do plurals
                        )
                    }
                ) { album ->
                    metadataState.mutablePropertiesMap["ALBUM"] = album
                }
                Spacer(Modifier.height(25.dp))

                Row {
                    EditTextField(
                        value = metadataState.mutablePropertiesMap["DATE"],
                        label = {
                            CuteText(
                                text = stringResource(R.string.year)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number
                    ) { year ->
                        metadataState.mutablePropertiesMap["DATE"] = year
                    }
                    EditTextField(
                        value = metadataState.mutablePropertiesMap["GENRE"],
                        label = {
                            CuteText(
                                text = stringResource(R.string.genre)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) { genre ->
                        metadataState.mutablePropertiesMap["GENRE"] = genre
                    }
                }
                Row {
                    EditTextField(
                        value = metadataState.mutablePropertiesMap["TRACKNUMBER"],
                        label = {
                            CuteText(
                                text = stringResource(R.string.track_nb),
                                modifier = Modifier.basicMarquee()
                            )
                        },
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number
                    ) { track ->
                        metadataState.mutablePropertiesMap["TRACKNUMBER"] = track
                    }
                    EditTextField(
                        value = metadataState.mutablePropertiesMap["DISCNUMBER"],
                        label = {
                            CuteText(
                                text = stringResource(R.string.disc_nb),
                                modifier = Modifier.basicMarquee()
                            )
                        },
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number
                    ) { disc ->
                        metadataState.mutablePropertiesMap["DISCNUMBER"] = disc
                    }
                }
                EditTextField(
                    value = metadataState.mutablePropertiesMap["LYRICS"],
                    label = {
                        CuteText(
                            text = "Lyrics",
                            modifier = Modifier.basicMarquee()
                        )
                    }
                ) { lyrics ->
                    metadataState.mutablePropertiesMap["LYRICS"] = lyrics
                }
            }
        }
        CuteNavigationButton(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(start = 15.dp)
        ) { onPopBackStack() }
        CuteActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 15.dp),
            imageVector = Icons.Rounded.Done
        ) {
            onEditMusic(
                listOf(uri),
                editSongLauncher
            )
        }
    }
}


@Composable
private fun EditTextField(
    modifier: Modifier = Modifier,
    value: String?,
    verticalPadding: Dp = 5.dp,
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
            .padding(horizontal = 10.dp, vertical = verticalPadding)
    )
}

