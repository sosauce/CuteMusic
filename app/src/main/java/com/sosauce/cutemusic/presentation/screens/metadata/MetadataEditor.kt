@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.metadata

import android.app.Activity
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.domain.actions.MetadataActions
import com.sosauce.cutemusic.presentation.shared_components.CuteActionButton
import com.sosauce.cutemusic.presentation.shared_components.CuteNavigationButton
import com.sosauce.cutemusic.presentation.shared_components.ThreadDivider
import com.sosauce.cutemusic.utils.ImageUtils
import com.sosauce.cutemusic.utils.selfAlignHorizontally

@Composable
fun MetadataEditor(
    trackUri: Uri,
    fileName: String,
    onNavigateUp: () -> Unit,
    metadataViewModel: MetadataViewModel
) {

    val metadataState by metadataViewModel.metadataState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val resources = LocalResources.current
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
                onNavigateUp()
            } else {
                Toast.makeText(
                    context,
                    resources.getString(R.string.error_saving),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FloatingActionButton(
                    onClick = onNavigateUp,
                    modifier = Modifier
                        .padding(start = 15.dp),
                    shape = MaterialShapes.Cookie9Sided.toShape(),
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    Icon(
                        painter = painterResource(R.drawable.back),
                        contentDescription = null
                    )
                }
                CuteActionButton(
                    modifier = Modifier.padding(end = 15.dp),
                    icon = R.drawable.check
                ) {
                    val intentSender = MediaStore.createWriteRequest(
                        context.contentResolver,
                        listOf(trackUri)
                    ).intentSender
                    editSongLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .imePadding()
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
                        painter = painterResource(R.drawable.close),
                        contentDescription = null
                    )
                }
                if (metadataState.art != null) {
                    AsyncImage(
                        model = ImageUtils.imageRequester(metadataState.art?.data, context),
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
                    initialValue = metadataState.mutablePropertiesMap["TITLE"],
                    label = {
                        Text(
                            text = stringResource(R.string.title)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.music_note_rounded),
                            contentDescription = null
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
                    Text(
                        text = "${stringResource(R.string.file_name)}: $fileName",
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 5.dp),
                        color = MaterialTheme.colorScheme.onBackground.copy(0.85f)
                    )
                }

                EditTextField(
                    initialValue = metadataState.mutablePropertiesMap["ARTIST"],
                    verticalPadding = 0.dp,
                    label = {
                        Text(
                            text = stringResource(R.string.artist)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.artist_rounded),
                            contentDescription = null
                        )
                    }
                ) { artist ->
                    metadataState.mutablePropertiesMap["ARTIST"] = artist
                }
                EditTextField(
                    initialValue = metadataState.mutablePropertiesMap["ALBUM"],
                    label = {
                        Text(
                            text = stringResource(R.string.album)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(androidx.media3.session.R.drawable.media3_icon_album),
                            contentDescription = null
                        )
                    }
                ) { album ->
                    metadataState.mutablePropertiesMap["ALBUM"] = album
                }
                Spacer(Modifier.height(25.dp))

                Row {
                    EditTextField(
                        initialValue = metadataState.mutablePropertiesMap["DATE"],
                        label = {
                            Text(
                                text = stringResource(R.string.year)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Number
                    ) { year ->
                        metadataState.mutablePropertiesMap["DATE"] = year
                    }
                    EditTextField(
                        initialValue = metadataState.mutablePropertiesMap["GENRE"],
                        label = {
                            Text(
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
                        initialValue = metadataState.mutablePropertiesMap["TRACKNUMBER"],
                        label = {
                            Text(
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
                        initialValue = metadataState.mutablePropertiesMap["DISCNUMBER"],
                        label = {
                            Text(
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
                    initialValue = metadataState.mutablePropertiesMap["LYRICS"],
                    label = {
                        Text(
                            text = stringResource(R.string.lyrics),
                            modifier = Modifier.basicMarquee()
                        )
                    },
                    imeAction = ImeAction.Default,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.lyrics_rounded),
                            contentDescription = null
                        )
                    }
                ) { lyrics ->
                    metadataState.mutablePropertiesMap["LYRICS"] = lyrics
                }
            }
        }
    }
}


@Composable
private fun EditTextField(
    modifier: Modifier = Modifier,
    initialValue: String?,
    verticalPadding: Dp = 5.dp,
    label: (@Composable () -> Unit)? = null,
    imeAction: ImeAction = ImeAction.Done,
    keyboardType: KeyboardType = KeyboardType.Unspecified,
    leadingIcon: @Composable (() -> Unit)? = null,
    returnModifiedValue: (String) -> Unit
) {


    OutlinedTextField(
        value = initialValue ?: "",
        onValueChange = { returnModifiedValue(it) },
        label = label,
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction,
            keyboardType = keyboardType
        ),
        leadingIcon = leadingIcon,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = verticalPadding)
    )
}

