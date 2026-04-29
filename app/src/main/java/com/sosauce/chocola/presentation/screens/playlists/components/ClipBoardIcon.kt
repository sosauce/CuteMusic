package com.sosauce.chocola.presentation.screens.playlists.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.presentation.screens.playlists.components.ClipboardIconStatus.Default
import com.sosauce.chocola.presentation.screens.playlists.components.ClipboardIconStatus.Error
import com.sosauce.chocola.presentation.screens.playlists.components.ClipboardIconStatus.Success
import com.sosauce.chocola.utils.barsContentTransform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ClipboardIconStatus {
    Default, Success, Error
}


class ClipboardIcon(
    private val scope: CoroutineScope
) {
    private val _status = MutableStateFlow(Default)
    val status = _status.asStateFlow()

    private var job: Job? = null

    fun setError() {
        job?.cancel()
        job = scope.launch {
            _status.value = Error
            delay(500)
            _status.value = Default
        }
    }

    fun setSuccess() {
        job = scope.launch {
            _status.value = Success
            delay(500)
            _status.value = Default
        }
    }
}
@Composable
fun ClipboardIconStatus.icon(defaultIcon: Int): Painter {
    return painterResource(
        when (this) {
            Default -> defaultIcon
            Success -> R.drawable.check
            Error -> R.drawable.close
        }
    )
}


@Composable
fun ClipboardIcon.Icon(
    defaultIcon: Int,
    onClick: () -> Unit
) {
    val status by this.status.collectAsState()

    AnimatedContent(
        targetState = status,
        transitionSpec = { barsContentTransform }
    ) { status ->
        val painter = status.icon(defaultIcon)

        Icon(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .clickable(onClick = onClick)
                .padding(5.dp)
        )
    }
}




@Composable
fun rememberClipboardIconController(): ClipboardIcon {
    val scope = rememberCoroutineScope()
    return remember { ClipboardIcon(scope) }
}
