package com.sosauce.cutemusic.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.PersonAddAlt
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Web
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.logic.AppBar
import com.sosauce.cutemusic.screens.landscape.AboutLandscape
import com.sosauce.cutemusic.ui.theme.GlobalFont

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AboutScreen(navController: NavController) {
    val iconsTint = MaterialTheme.colorScheme.onBackground
    val urlList = listOf(
        "https: github.com/sosauce/CuteMusic/issues",
        "https: github.com/sosauce?tab=repositories",
        "https: github.com/sosauce/CuteMusic/releases",
        "https: sosauce.github.io/",
        "https: github.com/sosauce/CuteCalc/issues/new?assignees=&labels=&projects=&template=feature_request.md&title=",
        "https: discord.gg/npvpaFUAHH"
    )
    val context = LocalContext.current
    val config = LocalConfiguration.current


    if (config.orientation != Configuration.ORIENTATION_PORTRAIT) {
        AboutLandscape(navController)
    } else {
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
        ) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Spacer(modifier = Modifier.height(90.dp))
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cute_music_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(150.dp)
                    )
                    Text(
                        text = "CuteMusic",
                        fontFamily = GlobalFont,
                        fontSize = 40.sp
                    )
                    Text(
                        text = "Beta 1 (Build_1)",
                        fontFamily = GlobalFont,
                        fontSize = 20.sp,
                        modifier = Modifier.offset(y = (-7).dp),
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

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Made with ❤️ by sosauce",
                        fontSize = 25.sp,
                        fontFamily = GlobalFont,
                        color = iconsTint
                    )
                }

                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(15.dp))
                val interactionSource = remember { MutableInteractionSource() }

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
                        modifier = Modifier.size(30.dp),
                        tint = iconsTint
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "All my projects",
                        fontSize = 25.sp,
                        fontFamily = GlobalFont,
                        color = iconsTint
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
                        modifier = Modifier.size(30.dp),
                        tint = iconsTint
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Me, I guess",
                        fontSize = 25.sp,
                        fontFamily = GlobalFont,
                        color = iconsTint
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
                        modifier = Modifier.size(30.dp),
                        tint = iconsTint
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Report a bug",
                        fontSize = 25.sp,
                        fontFamily = GlobalFont,
                        color = iconsTint
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
                        modifier = Modifier.size(30.dp),
                        tint = iconsTint
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Request a feature",
                        fontSize = 25.sp,
                        fontFamily = GlobalFont,
                        color = iconsTint
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clickable(interactionSource = interactionSource, indication = null) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlList[5]))
                            context.startActivity(intent)
                        }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.discord),
                        contentDescription = "discord logo",
                        modifier = Modifier.size(30.dp),
                        tint = iconsTint
                    )
                }

            }
        }
    }

}