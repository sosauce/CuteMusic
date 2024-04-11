package com.sosauce.cutemusic.screens.landscape

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.PersonAddAlt
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Web
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.logic.AppBar
import com.sosauce.cutemusic.ui.theme.GlobalFont

@Composable
fun AboutLandscape(navController: NavController) {
    ALContent(navController)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun ALContent(navController: NavController) {

    val urlList = listOf(
        "https:github.com/sosauce/CuteMusic/issues",
        "https:github.com/sosauce?tab=repositories",
        "https:github.com/sosauce/CuteMusic/releases",
        "https:sosauce.github.io/",
        "https:github.com/sosauce/CuteCalc/issues/new?assignees=&labels=&projects=&template=feature_request.md&title=",
        "https:discord.gg/npvpaFUAHH"
    )
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }


    Scaffold(
        topBar = {
            AppBar(
                title = "About",
                showBackArrow = true,
                navController = navController,
                showMenuIcon = false,
                showSearchIcon = false,
                onSearchBarStateChanged = null
            )
        },
    ) {values ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(values)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(45.dp)
            ) {
                Row {
                    Image(
                        painter = painterResource(id = R.drawable.cute_music_icon),
                        contentDescription = null,
                        modifier = Modifier.size(200.dp)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceAround) {
                        Text(
                            text = "CuteMusic",
                            fontFamily = GlobalFont,
                            fontSize = 40.sp
                        )
                        Text(
                            text = "Beta 1 (Build_1)",
                            fontFamily = GlobalFont,
                            fontSize = 20.sp
                        )
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlList[2]))
                                context.startActivity(intent)
                            }
                        ) {
                            Text(text = "Check for updates", fontFamily = GlobalFont)
                        }
                    }
                }
                Row(Modifier.fillMaxSize().background(Color.Red)) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .clickable(interactionSource = interactionSource, indication = null) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlList[1]))
                                context.startActivity(intent)
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.StarOutline,
                            contentDescription = "all my projects",
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "All my projects",
                            fontSize = 25.sp,
                            fontFamily = GlobalFont
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .clickable(interactionSource = interactionSource, indication = null) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlList[3]))
                                context.startActivity(intent)
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Web,
                            contentDescription = "my website",
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Me, I guess",
                            fontSize = 25.sp,
                            fontFamily = GlobalFont
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .clickable(interactionSource = interactionSource, indication = null) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlList[0]))
                                context.startActivity(intent)
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.BugReport,
                            contentDescription = "report a bug",
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Report a bug",
                            fontSize = 25.sp,
                            fontFamily = GlobalFont
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .clickable(interactionSource = interactionSource, indication = null) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlList[4]))
                                context.startActivity(intent)
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PersonAddAlt,
                            contentDescription = "request a feature",
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Request a feature",
                            fontSize = 25.sp,
                            fontFamily = GlobalFont
                        )
                    }
                }
            }
        }
    }

}