package com.sosauce.cutemusic.ui.shared_components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.utils.CurrentScreen

@Composable
fun ScreenSelection(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val items = listOf(
        NavigationItem(
            title = R.string.music,
            navigateTo = Screen.Main,
            icon = painterResource(R.drawable.music_note_rounded)
        ),
        NavigationItem(
            title = R.string.albums,
            navigateTo = Screen.Albums,
            icon = painterResource(androidx.media3.session.R.drawable.media3_icon_album)
        ),
        NavigationItem(
            title = R.string.artists,
            navigateTo = Screen.Artists,
            icon = painterResource(R.drawable.artist_rounded)
        ),
        NavigationItem(
            title = R.string.playlists,
            navigateTo = Screen.Playlists,
            icon = rememberVectorPainter(Icons.AutoMirrored.Rounded.QueueMusic)
        )

    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            items.forEach { navigationItem ->

                val bgColor by animateColorAsState(
                    targetValue = if (navigationItem.navigateTo.toString() == CurrentScreen.screen) MaterialTheme.colorScheme.surfaceContainerHigh else Color.Transparent
                )

                CuteDropdownMenuItem(
                    onClick = { onNavigate(navigationItem.navigateTo) },
                    text = { CuteText(stringResource(navigationItem.title)) },
                    leadingIcon = {
                        Icon(
                            painter = navigationItem.icon,
                            contentDescription = stringResource(navigationItem.title)
                        )
                    },
                    modifier = Modifier
                        .padding(2.dp)
                        .background(
                            color = bgColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                )
            }
        }
    }
}

@Immutable
data class NavigationItem(
    val title: Int,
    val navigateTo: Screen,
    val icon: Painter,
)