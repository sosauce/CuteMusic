package com.sosauce.cutemusic.components

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.format.Formatter
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.screens.CommonArtwork
import com.sosauce.cutemusic.ui.theme.GlobalFont
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun BottomSheetContent(
	music: Music,
	modifier: Modifier = Modifier,
	bitmap: Bitmap? = null,
) {
	val context = LocalContext.current

	var fileType by remember { mutableStateOf<String?>(null) }
	var fileSize by remember { mutableStateOf("unknown") }
	var fileBitrate by remember { mutableStateOf("unknwon") }

	LaunchedEffect(music) {
		fileType = context.contentResolver.getType(music.uri)
		fileBitrate = getFileBitrate(context, music.uri)
		val size = getFileSize(context, music.uri)
		fileSize = Formatter.formatShortFileSize(context, size)
	}

	val deleteImageLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.StartIntentSenderForResult(),
		onResult = {
			//	if (it.resultCode == Activity.RESULT_OK)
			//  handle some toast to show the user its deleted or not
			//	else  same toast with the problem
		}
	)

	Column(modifier = modifier) {

		Card(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 2.dp),
			shape = RoundedCornerShape(
				topStart = 24.dp,
				topEnd = 24.dp,
				bottomStart = 4.dp,
				bottomEnd = 4.dp
			),
			colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh),
		) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				CommonArtwork(
					bitmap = bitmap, contentDescription = null,
					modifier = Modifier
						.size(100.dp)
						.padding(15.dp)
						.clip(RoundedCornerShape(15)),
				)
				Column {
					Text(
						text = music.title,
						fontFamily = GlobalFont
					)
					Text(
						text = music.artist,
						fontFamily = GlobalFont
					)
				}
				Spacer(modifier = Modifier.weight(1f))
				IconButton(
					onClick = {
						createDeleteRequest(
							uri = music.uri,
							intentSenderLauncher = deleteImageLauncher,
							context = context
						)
					},
				) {
					Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = null)
				}
			}
		}
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 2.dp),
			shape = RoundedCornerShape(
				topStart = 4.dp,
				topEnd = 4.dp,
				bottomStart = 24.dp,
				bottomEnd = 24.dp
			),
			colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh),
		) {
			Column(modifier = Modifier.padding(15.dp)) {
				Text(
					text = stringResource(id = R.string.size, fileSize),
					fontFamily = GlobalFont,
					modifier = Modifier.padding(bottom = 5.dp)
				)
				Text(
					text = stringResource(id = R.string.bitrate, fileBitrate),
					fontFamily = GlobalFont,
					modifier = Modifier.padding(bottom = 5.dp)
				)
				Text(
					text = stringResource(id = R.string.type, fileType ?: ""),
					fontFamily = GlobalFont,
					modifier = Modifier.padding(bottom = 5.dp)
				)
			}
		}
	}
}

private fun getFileSize(context: Context, uri: Uri): Long {
	var size: Long = 0
	context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
		val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
		if (cursor.moveToFirst()) {
			size = cursor.getLong(sizeIndex)
		}
	}
	return size
}

private fun getFileBitrate(context: Context, uri: Uri): String {
	val retriever = MediaMetadataRetriever()
	return try {
		retriever.setDataSource(context, uri)
		val bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
		retriever.release()
		bitrate?.toInt()?.div(1000)?.toString()?.plus(" kbps") ?: "Unknown"
	} catch (e: Exception) {
		"Unknown"
	}
}

private fun createDeleteRequest(
	uri: Uri,
	intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>,
	context: Context
) {
	val coroutineScope = CoroutineScope(Dispatchers.Main)
	coroutineScope.launch {
		try {
			context.contentResolver.delete(uri, null, null)
		} catch (e: SecurityException) {
			// This exception will only be thrown from Android 11
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
				val intentSender = MediaStore.createDeleteRequest(
					context.contentResolver,
					listOf(uri)
				).intentSender

				intentSenderLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
			}
		} catch (e: Exception) {
			Log.e(
				ContentValues.TAG,
				"Error trying to delete image: ${e.message} ${e.stackTrace.joinToString()}"
			)
		}
	}
}
