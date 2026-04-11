@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.chocola.presentation.shared_components

import android.content.ContentProviderOperation
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.data.models.CuteTrack
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.screens.playlists.components.PlaylistPicker
import com.sosauce.chocola.utils.rememberSearchbarMaxFloatValue
import com.sosauce.chocola.utils.rememberSearchbarRightPadding
import com.sosauce.sweetselect.SweetSelectState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun <T> SelectedBarSurface(
    modifier: Modifier = Modifier,
    items: List<T>,
    multiSelectState: SweetSelectState<T>,
    actions: @Composable (RowScope.() -> Unit)
) {
    Column(
        modifier = modifier
            .navigationBarsPadding()
            .fillMaxWidth(rememberSearchbarMaxFloatValue())
            .padding(end = rememberSearchbarRightPadding())
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = multiSelectState::clearSelected,
                shapes = ButtonDefaults.shapes()
            ) {
                Icon(
                    painter = painterResource(R.drawable.close),
                    contentDescription = null
                )
                Spacer(Modifier.width(5.dp))
                Text("${multiSelectState.selectedItems.size}")
            }
            TextButton(
                onClick = {
                    if (multiSelectState.selectedItems.size == items.size) {
                        multiSelectState.clearSelected()
                    } else {
                        multiSelectState.toggleAll(items)
                    }
                },
                shapes = ButtonDefaults.shapes()

            ) {

                val icon =
                    if (items.size == multiSelectState.selectedItems.size) R.drawable.unselect_all else R.drawable.select_all

                Icon(
                    painter = painterResource(icon),
                    contentDescription = null
                )
                Spacer(Modifier.width(5.dp))

                val text =
                    if (items.size == multiSelectState.selectedItems.size) R.string.unselect_all else R.string.select_all

                Text(stringResource(text))
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) { actions() }
    }
}

@Composable
fun TracksSelectedBar(
    modifier: Modifier = Modifier,
    tracks: List<CuteTrack>,
    multiSelectState: SweetSelectState<CuteTrack>,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    SelectedBarSurface(
        modifier = modifier,
        items = tracks,
        multiSelectState = multiSelectState
    ) {
        var showPlaylistDialog by remember { mutableStateOf(false) }
        val deleteSongLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {}

        if (showPlaylistDialog) {
            PlaylistPicker(
                mediaId = multiSelectState.selectedItems.map { it.mediaId },
                onDismissRequest = { showPlaylistDialog = false },
                onAddingFinished = multiSelectState::clearSelected
            )
        }


        IconButton(
            onClick = { showPlaylistDialog = true },
            shapes = IconButtonDefaults.shapes()
        ) {
            Icon(
                painter = painterResource(R.drawable.playlist_add),
                contentDescription = null
            )
        }
        IconButton(
            onClick = {
                onHandlePlayerActions(PlayerActions.AddToQueue(multiSelectState.selectedItems.toList()))
                multiSelectState.clearSelected()
            },
            shapes = IconButtonDefaults.shapes()
        ) {
            Icon(
                painter = painterResource(R.drawable.add_to_queue),
                contentDescription = null
            )
        }
        IconButton(
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val intentSender = MediaStore.createDeleteRequest(
                        context.contentResolver,
                        multiSelectState.selectedItems.map { it.uri }
                    ).intentSender

                    deleteSongLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                } else {
                    scope.launch(Dispatchers.IO) {
                        val ops = arrayListOf<ContentProviderOperation>()

                        multiSelectState.selectedItems.forEach { item ->
                            ops.add(
                                ContentProviderOperation.newDelete(item.uri).build()
                            )
                        }

                        runCatching {
                            context.contentResolver.applyBatch(MediaStore.AUTHORITY, ops)
                        }.onFailure {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            },
            shapes = IconButtonDefaults.shapes(),
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.trash_rounded_filled),
                contentDescription = null
            )
        }
    }



}