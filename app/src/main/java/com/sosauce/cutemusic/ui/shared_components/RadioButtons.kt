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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.ui.navigation.Screen

@Composable
fun ScreenSelection(
    onNavigationItemClicked: (Int, NavigationItem) -> Unit,
    selectedIndex: Int
) {

    val context = LocalContext.current

    val items = listOf(
        NavigationItem(
            title = context.getString(R.string.music),
            navigateTo = Screen.Main,
            activeIcon = Icons.Rounded.MusicNote,
            notActiveIcon = Icons.Outlined.MusicNote
        ),
        NavigationItem(
            title = stringResource(R.string.albums),
            navigateTo = Screen.Albums,
            activeIcon = Icons.Rounded.Album,
            notActiveIcon = Icons.Outlined.Album
        ),
        NavigationItem(
            title = stringResource(R.string.artists),
            navigateTo = Screen.Artists,
            activeIcon = Icons.Rounded.Person,
            notActiveIcon = Icons.Outlined.Person
        )

    )


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
                    imageVector = navigationItem.activeIcon,
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
    val activeIcon: ImageVector,
    val notActiveIcon: ImageVector,
)