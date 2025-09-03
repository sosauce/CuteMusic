package com.sosauce.cutemusic.presentation.screens.settings.compenents

import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.presentation.shared_components.CuteText
import java.io.File

@Composable
fun FolderItem(
    modifier: Modifier = Modifier,
    folder: String,
    topDp: Dp,
    bottomDp: Dp,
    actionButton: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(
            topStart = topDp,
            topEnd = topDp,
            bottomStart = bottomDp,
            bottomEnd = bottomDp
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(R.drawable.folder_rounded),
                contentDescription = null,
                modifier = Modifier.size(33.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                CuteText(
                    text = File(folder).name,
                    fontSize = 18.sp
                )
                CuteText(
                    text = folder,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    modifier = Modifier.basicMarquee()
                )
            }
            actionButton()
        }
    }
}