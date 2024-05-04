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
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.sosauce.cutemusic.activities.MusicViewModel
import com.sosauce.cutemusic.ui.theme.GlobalFont

@Composable
fun BottomBar(
    navController: NavController,
    viewModel: MusicViewModel
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
            navigateTo = "ArtistsScreen",
            activeIcon = Icons.Default.Person,
            notActiveIcon = Icons.Outlined.Person
        )

    )


    NavigationBar {
        items.forEachIndexed { index, item ->
            val selected = viewModel.selectedItem == index
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.navigateTo) {
                        viewModel.selectedItem = index
                        launchSingleTop = true
                        restoreState = true

                    }

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

@Immutable
data class NavigationItem(
    val title: String,
    val navigateTo: String,
    val activeIcon: ImageVector,
    val notActiveIcon: ImageVector,
)