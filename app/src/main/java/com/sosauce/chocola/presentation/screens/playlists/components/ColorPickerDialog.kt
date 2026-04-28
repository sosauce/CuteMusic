package com.sosauce.chocola.presentation.screens.playlists.components

import android.content.ClipData
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.sosauce.chocola.R
import com.sosauce.chocola.presentation.screens.playlists.components.ClipboardIconStatus.Default
import com.sosauce.chocola.presentation.screens.playlists.components.ClipboardIconStatus.Error
import com.sosauce.chocola.presentation.screens.playlists.components.ClipboardIconStatus.Success
import com.sosauce.chocola.utils.barsContentTransform
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private enum class ClipboardIconStatus {
    Default, Success, Error
}

@Composable
private fun ClipboardIconStatus.icon(defaultIcon: Int): Painter {
    return painterResource(
        when (this) {
            Default -> defaultIcon
            Success -> R.drawable.check
            Error -> R.drawable.close
        }
    )
}

@Composable
fun ColorPickerDialog(
    initialColor: Color? = null,
    onDismissRequest: () -> Unit,
    onAddNewColor: (Int) -> Unit
) {
    val clipboardManager = LocalClipboard.current
    val scope = rememberCoroutineScope()

    val controller = rememberColorPickerController()
    var hexCode by remember { mutableStateOf("") }

    var pastingIconStatus by remember { mutableStateOf(Default) }
    LaunchedEffect(pastingIconStatus) { // Use launched effect to cancel previous jobs in case of user spamming
        if (pastingIconStatus != Default) {
            delay(500)
            pastingIconStatus = Default
        }
    }

    var copyIconStatus by remember { mutableStateOf(Default) }
    LaunchedEffect(copyIconStatus) {
        if (copyIconStatus != Default) {
            delay(500)
            copyIconStatus = Default
        }
    }

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
            val context = LocalContext.current

            fun copyHexTextToClipboard() {
                scope.launch {
                    try {
                        clipboardManager.setClipEntry(
                            ClipEntry(
                                ClipData.newPlainText(
                                    "hexCode",
                                    "#$hexCode"
                                )
                            )
                        )
                        copyIconStatus = Success
                    } catch (e: Exception) {
                        Log.e("ColorPicker", "Failed to copy hexCode (${hexCode}) to clipboard", e)
                        copyIconStatus = Error
                    }
                }
            }

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

                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = null,
                            onClick = ::copyHexTextToClipboard
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(controller.selectedColor.value)
                    )
                    Text("#$hexCode")
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedContent(
                        targetState = copyIconStatus,
                        transitionSpec = { barsContentTransform }
                    ) { status ->

                        val painter = status.icon(R.drawable.copy)

                        Icon(
                            painter = painter,
                            contentDescription = null,
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .clickable(onClick = ::copyHexTextToClipboard)
                                .padding(5.dp)
                        )

                    }
                    AnimatedContent(
                        targetState = pastingIconStatus,
                        transitionSpec = { barsContentTransform }
                    ) { status ->
                        val painter = status.icon(R.drawable.paste)

                        Icon(
                            painter = painter,
                            contentDescription = null,
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .clickable {
                                    val clip = clipboardManager.nativeClipboard.primaryClip ?: return@clickable
                                    if (clip.itemCount == 0) return@clickable
                                    clip.getItemAt(0).coerceToText(context)?.toString()?.let { pasted ->
                                        try {
                                            if (pasted.startsWith("#") && pasted.length == 9) {
                                                controller.selectByColor(Color(pasted.toColorInt()), true)
                                                pastingIconStatus = Success
                                            } else {
                                                pastingIconStatus = Error
                                            }
                                        } catch (_: Exception) {
                                            // Decrypt failed from clipboard, no need to log that
                                            pastingIconStatus = Error
                                        }
                                    }
                                }
                                .padding(5.dp)
                        )
                    }
                }
            }
        }
    )
}
