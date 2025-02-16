package com.sosauce.cutemusic.ui.shared_components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.utils.thenIf

@Composable
fun ScreenSelection(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onNavigationItemClicked: (Screen) -> Unit,
    currentScreen: String
) {

    val context = LocalContext.current
    val surfaceContainer = MaterialTheme.colorScheme.surfaceContainer
    val items = listOf(
        NavigationItem(
            title = context.getString(R.string.music),
            navigateTo = Screen.Main,
            icon = painterResource(R.drawable.music_note_rounded)
        ),
        NavigationItem(
            title = stringResource(R.string.albums),
            navigateTo = Screen.Albums,
            icon = painterResource(androidx.media3.session.R.drawable.media3_icon_album)
        ),
        NavigationItem(
            title = stringResource(R.string.artists),
            navigateTo = Screen.Artists,
            icon = painterResource(R.drawable.artist_rounded)
        ),
        NavigationItem(
            title = "Playlists",
            navigateTo = Screen.Playlists,
            icon = rememberVectorPainter(Icons.AutoMirrored.Rounded.QueueMusic)
        )

    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.width(180.dp),
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            items.forEach { navigationItem ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(5.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigationItemClicked(navigationItem.navigateTo) }
                        .thenIf(navigationItem.navigateTo.toString() == currentScreen) {
                            Modifier.background(
                                color = surfaceContainer,
                                shape = RoundedCornerShape(12.dp)
                            )
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = navigationItem.icon,
                        contentDescription = navigationItem.title,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                    CuteText(
                        text = navigationItem.title,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                }
            }
        }
    }


}

@Immutable
data class NavigationItem(
    val title: String,
    val navigateTo: Screen,
    val icon: Painter,
)