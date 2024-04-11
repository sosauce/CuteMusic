@file:OptIn(ExperimentalMaterial3Api::class)

package com.sosauce.cutemusic.components

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.audio.Music
import com.sosauce.cutemusic.audio.getMusicArt
import com.sosauce.cutemusic.ui.theme.GlobalFont

@Composable
fun BottomSheetContent(music: Music) {
    val showDeleteDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    var showMusicDetailsInstead by rememberSaveable { mutableStateOf(false) }
    var art: ByteArray? by remember { mutableStateOf(byteArrayOf()) }

    LaunchedEffect(music.uri) {
        art = getMusicArt(context, music)
    }


    if (showMusicDetailsInstead) {
        DetailsBottomSheet(music) { showMusicDetailsInstead = false }
    } else {
        if (showDeleteDialog.value) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                DeleteDialog(showDeleteDialog, music.uri)
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = art ?: R.drawable.cute_music_icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(80.dp)
                    .clip(RoundedCornerShape(10))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = music.title,
                    fontFamily = GlobalFont
                )
                Text(
                    text = music.artist,
                    fontFamily = GlobalFont
                )
            }
        }
        Spacer(modifier = Modifier.height(25.dp))
        Column(Modifier.padding(10.dp)) {  //column with all actions i.e delete etc..
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showMusicDetailsInstead = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "details",
                    modifier = Modifier.padding(10.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Details",
                    fontFamily = GlobalFont
                )
            }
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable {
//                        showDeleteDialog.value = true
//                    },
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(
//                    imageVector = Icons.Outlined.DeleteOutline,
//                    contentDescription = "delete",
//                    modifier = Modifier.padding(10.dp),
//                    tint = MaterialTheme.colorScheme.error
//                )
//                Spacer(modifier = Modifier.width(10.dp))
//                Text(
//                    text = "Delete from device",
//                    fontFamily = GlobalFont,
//                    color = MaterialTheme.colorScheme.error
//                )
//            }
        }
    }



}
