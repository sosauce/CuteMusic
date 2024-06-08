package com.sosauce.cutemusic.logic

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
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import com.sosauce.cutemusic.logic.navigation.Screen
import com.sosauce.cutemusic.ui.theme.GlobalFont

@Composable
fun BottomBar(
	selectedIndex: Int,
	onNavigationItemClicked: (Int, NavigationItem) -> Unit,
) {

	val items = remember {
		listOf(
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
	}


	NavigationBar {
		items.forEachIndexed { index, item ->
			val selected = selectedIndex == index
			NavigationBarItem(
				selected = selected,
				onClick = { onNavigationItemClicked(index, item) },
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
	val navigateTo: Screen,
	val activeIcon: ImageVector,
	val notActiveIcon: ImageVector,
)