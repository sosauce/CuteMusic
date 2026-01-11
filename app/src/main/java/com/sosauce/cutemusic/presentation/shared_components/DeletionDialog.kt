@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.shared_components

import android.app.Activity
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.models.CuteTrack
import com.sosauce.cutemusic.data.models.Playlist
import com.sosauce.cutemusic.domain.actions.PlaylistActions
import com.sosauce.cutemusic.utils.ImageUtils

/**
 * A dialog that should be used as a confirmation to delete
 */
@Composable
fun DeletionDialog(
    track: CuteTrack,
    onDismissRequest: () -> Unit
) {

    val context = LocalContext.current
    val deleteSongLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode != Activity.RESULT_OK) {
                Toast.makeText(
                    context,
                    context.getString(R.string.error_deleting_song),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.are_u_sure)) },
        confirmButton = {
            TextButton(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val intentSender = MediaStore.createDeleteRequest(
                            context.contentResolver,
                            listOf(track.uri)
                        ).intentSender

                        deleteSongLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                    } else {
                        context.contentResolver.delete(track.uri, null, null)
                    }
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.trash_rounded),
                contentDescription = null
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = ImageUtils.imageRequester(track.artUri, context),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(15.dp)
                        .clip(RoundedCornerShape(15)),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    modifier = Modifier.basicMarquee()
                )
                Text(
                    text = track.artist,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmallEmphasized,
                    modifier = Modifier.basicMarquee()
                )

            }
        }
    )
}

@Composable
fun PlaylistDeletionDialog(
    playlist: Playlist,
    onDismissRequest: () -> Unit,
    onHandlePlaylistAction: (PlaylistActions) -> Unit
) {

    val context = LocalContext.current
    val deleteSongLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode != Activity.RESULT_OK) {
                Toast.makeText(
                    context,
                    context.getString(R.string.error_deleting_song),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.are_u_sure)) },
        confirmButton = {
            TextButton(
                onClick = {
                    onHandlePlaylistAction(PlaylistActions.DeletePlaylist(playlist))
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.trash_rounded),
                contentDescription = null
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = playlist.emoji,
                    fontSize = 50.sp
                )
                Text(
                    text = playlist.name,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmallEmphasized,
                    modifier = Modifier.basicMarquee()
                )

            }
        }
    )

}