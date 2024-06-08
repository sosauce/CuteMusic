package com.sosauce.cutemusic.screens.utils

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

@Composable
fun rememberHasReadAudioPermissions(): Boolean {

	val context = LocalContext.current

	var readExternalStorage by remember {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) mutableStateOf(
			ContextCompat.checkSelfPermission(
				context, Manifest.permission.READ_EXTERNAL_STORAGE
			) == PermissionChecker.PERMISSION_GRANTED
		)
		else mutableStateOf(true)
	}

	var readAudioPermission by remember {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) mutableStateOf(
			ContextCompat.checkSelfPermission(
				context, Manifest.permission.READ_MEDIA_AUDIO
			) == PermissionChecker.PERMISSION_GRANTED
		)
		else mutableStateOf(true)
	}

	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestPermission()
	) { granted ->
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
			readAudioPermission = granted
		else readExternalStorage = granted
	}

	if (!readAudioPermission || !readExternalStorage) {
		LaunchedEffect(Unit) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
				launcher.launch(Manifest.permission.READ_MEDIA_AUDIO)
			else launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
		}
	}

	return readExternalStorage && readAudioPermission
}
