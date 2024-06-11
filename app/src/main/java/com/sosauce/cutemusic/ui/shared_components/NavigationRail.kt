package com.sosauce.cutemusic.ui.shared_components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.theme.GlobalFont

@Composable
fun CuteNavigationRail(
    selectedIndex: Int,
    onNavigationItemClicked: (Int, NavigationItem) -> Unit
) {
    val items = listOf(
        NavigationItem(
            title = "Songs",
            navigateTo = Screen.Main,
            activeIcon = Icons.Default.MusicNote,
            notActiveIcon = Icons.Outlined.MusicNote
        ),
        NavigationItem(
            title = "Albums",
            navigateTo = Screen.Albums,
            activeIcon = Icons.Default.Album,
            notActiveIcon = Icons.Outlined.Album
        ),
        NavigationItem(
            title = "Artists",
            navigateTo = Screen.Artists,
            activeIcon = Icons.Default.Person,
            notActiveIcon = Icons.Outlined.Person
        )

    )



    NavigationRail {

        items.forEachIndexed { index, item ->
            val selected =
                selectedIndex == index
            NavigationRailItem(
                selected = selected,
                onClick = { onNavigationItemClicked(index, item) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.activeIcon else item.notActiveIcon,
                        contentDescription = "Icon"
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontFamily = GlobalFont
                    )
                },
                alwaysShowLabel = false
            )
        }

    }
}