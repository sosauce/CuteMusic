package com.sosauce.cutemusic.logic

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.sosauce.cutemusic.ui.theme.GlobalFont

@Composable
fun BottomBar(
    navController: NavController
) {

    val items = listOf(
        NavigationItem(
            title = "Songs",
            navigateTo = "MainScreen",
            activeIcon = Icons.Default.MusicNote,
            notActiveIcon = Icons.Outlined.MusicNote
        ),
        NavigationItem(
            title = "Albums",
            navigateTo = "AlbumsScreen",
            activeIcon = Icons.Default.Album,
            notActiveIcon = Icons.Outlined.Album
        ),
        NavigationItem(
            title = "Artists",
            navigateTo = "",
            activeIcon = Icons.Default.Person,
            notActiveIcon = Icons.Outlined.Person
        )

    )

    var selectedItem by rememberSaveable { mutableIntStateOf(0) }

    NavigationBar(
        modifier = Modifier.clickable { navController.navigate("NowPlaying") }
    ) {
        items.forEachIndexed{index, item ->
            val selected = selectedItem == index
            NavigationBarItem(
                selected = selected,
                onClick = {
                    selectedItem = index
                    navController.navigate(item.navigateTo) { launchSingleTop = true }
                          },
                icon = {
                    Icon(
                        imageVector = if (selected) item.activeIcon else item.notActiveIcon,
                        contentDescription = item.title
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

data class NavigationItem(
    val title: String,
    val navigateTo: String,
    val activeIcon: ImageVector,
    val notActiveIcon: ImageVector,
)