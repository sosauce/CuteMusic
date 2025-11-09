package com.sosauce.cutemusic.presentation.screens.playlists.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.sosauce.cutemusic.R


@Composable
fun ColorPickerDialog(
    initialColor: Color? = null,
    onDismissRequest: () -> Unit,
    onAddNewColor: (Int) -> Unit
) {

    val controller = rememberColorPickerController()
    var hexCode by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = {
            Icon(
                painter = painterResource(R.drawable.colorize_filled),
                contentDescription = null
            )
        },
        title = { Text(stringResource(R.string.color_picker)) },
        confirmButton = {
            TextButton(
                onClick = {
                    onAddNewColor(controller.selectedColor.value.toArgb())
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                HsvColorPicker(
                    modifier = Modifier.height(250.dp),
                    controller = controller,
                    onColorChanged = {
                        hexCode = it.hexCode
                    },
                    initialColor = initialColor
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(controller.selectedColor.value)
                )
                Text("#$hexCode")
            }
        }
    )
}