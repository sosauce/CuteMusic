@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.sosauce.cutemusic.presentation.screens.setup

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sosauce.cutemusic.R

@Composable
fun SetupPermissions(
    hasPermission: Boolean,
    onUpdateHasPermission: (Boolean) -> Unit
) {

    val musicPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = onUpdateHasPermission
    )


    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
//        Box(
//            modifier = Modifier
//                .size(350.dp)
//                .background(
//                    color = MaterialTheme.colorScheme.surfaceContainer,
//                    shape = MaterialShapes.Cookie9Sided.toShape()
//                )
//        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (hasPermission) {
                Text(stringResource(R.string.permission_granted))
            } else {
                Text(stringResource(R.string.permission_needed))
                Button(
                    onClick = {
                        val permission =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                Manifest.permission.READ_MEDIA_AUDIO
                            } else {
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            }
                        musicPermission.launch(permission)
                    },
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text(stringResource(R.string.request_permission))
                }
            }
        }
    }
}
