package com.sosauce.cutemusic.ui.shared_components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.data.datastore.rememberShowAlbumsTab
import com.sosauce.cutemusic.data.datastore.rememberShowArtistsTab
import com.sosauce.cutemusic.data.datastore.rememberShowFoldersTab
import com.sosauce.cutemusic.ui.navigation.Screen

@Composable
fun ScreenSelection(
    onNavigationItemClicked: (Int, NavigationItem) -> Unit,
    selectedIndex: Int
) {

    val context = LocalContext.current
    val showAlbumsTab by rememberShowAlbumsTab()
    val showArtistsTab by rememberShowArtistsTab()
    val showFoldersTab by rememberShowFoldersTab()


    val items = mutableListOf<NavigationItem>().apply {
        add(
            NavigationItem(
                title = context.getString(R.string.music),
                navigateTo = Screen.Main,
                icon = painterResource(R.drawable.music_note_rounded)
            )
        )
        if (showAlbumsTab) {
            add(
                NavigationItem(
                    title = stringResource(R.string.albums),
                    navigateTo = Screen.Albums,
                    icon = painterResource(androidx.media3.session.R.drawable.media3_icon_album)
                )
            )
        }
        if (showArtistsTab) {
            add(
                NavigationItem(
                    title = stringResource(R.string.artists),
                    navigateTo = Screen.Artists,
                    icon = painterResource(R.drawable.artist_rounded)
                )
            )
        }
        if (showFoldersTab) {
            add(
                NavigationItem(
                    title = stringResource(R.string.folders),
                    navigateTo = Screen.AllFolders,
                    icon = painterResource(R.drawable.folder_rounded)
                )
            )
        }
    }



    Column(
        verticalArrangement = Arrangement.Center
    ) {
        items.forEachIndexed { index, navigationItem ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(5.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onNavigationItemClicked(index, navigationItem) }
                    .then(
                        if (index == selectedIndex) {
                            Modifier.background(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(12.dp)
                            )
                        } else {
                            Modifier
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = navigationItem.icon,
                    contentDescription = navigationItem.title,
                    modifier = Modifier.padding(start = 15.dp)
                )
                CuteText(
                    text = navigationItem.title,
                    modifier = Modifier.padding(start = 15.dp),

                    )
            }
        }
    }
}

@Immutable
data class NavigationItem(
    val title: String,
    val navigateTo: Screen,
    val icon: Painter
)