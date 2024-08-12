package com.sosauce.cutemusic.ui.screens.artist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.domain.model.Artist
import com.sosauce.cutemusic.ui.customs.textCutter
import com.sosauce.cutemusic.ui.navigation.Screen
import com.sosauce.cutemusic.ui.screens.main.MusicListItem
import com.sosauce.cutemusic.ui.shared_components.CuteNavigationRail
import com.sosauce.cutemusic.ui.shared_components.NavigationItem
import com.sosauce.cutemusic.ui.shared_components.PostViewModel
import com.sosauce.cutemusic.ui.theme.GlobalFont
import com.sosauce.cutemusic.utils.ImageUtils

@Composable
fun ArtistsScreenLandscape(
    navController: NavController,
    artists: List<Artist>,
    postViewModel: PostViewModel,
    bottomBarIndex: Int,
    onBottomBarNavigation: (Int, NavigationItem) -> Unit,
    chargePVMLists: (name: String) -> Unit,
    onNavigate: (Screen) -> Unit
) {

    Scaffold { values ->

        if (artists.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(values)
            ) {
                Text(
                    text = stringResource(id = R.string.no_artists_found),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontFamily = GlobalFont
                )

            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(values)
                    .padding(start = 80.dp),
                verticalArrangement = Arrangement.Top
            ) {
                items(artists, key = { it.id }) { artist ->
                    ArtistInfoList(artist) {
                        chargePVMLists(artist.name)
                        onNavigate(Screen.ArtistsDetails(artist.id))
                    }
                }
            }
        }

        CuteNavigationRail(
            selectedIndex = bottomBarIndex,
            onNavigationItemClicked = onBottomBarNavigation
        )
    }
    
}
