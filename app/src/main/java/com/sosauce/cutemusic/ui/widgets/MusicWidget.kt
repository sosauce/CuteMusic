package com.sosauce.cutemusic.ui.widgets

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.ImageProvider
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentWidth
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.sosauce.cutemusic.R
import com.sosauce.cutemusic.utils.WIDGET_NEW_DATA
import com.sosauce.cutemusic.utils.WIDGET_NEW_IS_PLAYING
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


val CURRENTLY_PLAYING_WIDGET = stringPreferencesKey("CURRENTLY_PLAYING_WIDGET")
val CURRENT_ARTIST_WIDGET = stringPreferencesKey("CURRENT_ARTIST_WIDGET")
val CURRENT_ART_URI_WIDGET = stringPreferencesKey("CURRENT_ART_URI_WIDGET")
val IS_CURRENTLY_PLAYING_WIDGET = booleanPreferencesKey("IS_CURRENTLY_PLAYING_WIDGET")


object MusicWidget : GlanceAppWidget() {


    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {

            val currentlyPlaying = currentState(CURRENTLY_PLAYING_WIDGET)
            val currentArtist = currentState(CURRENT_ARTIST_WIDGET)
            val currentArtUri = currentState(CURRENT_ART_URI_WIDGET)
            val isCurrentlyPlaying = currentState(IS_CURRENTLY_PLAYING_WIDGET) == true



            Row(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(GlanceTheme.colors.background)
            ) {
                Image(
                    provider = ImageProvider(currentArtUri?.toUri() ?: Uri.EMPTY),
                    contentDescription = null,
                    modifier = GlanceModifier
                        .wrapContentWidth()
                        .fillMaxHeight()
                        .cornerRadius(5.dp), // Only A12 +, sad
                    contentScale = ContentScale.Fit
                )
                Spacer(GlanceModifier.width(10.dp))
                Column(
                    modifier = GlanceModifier.fillMaxSize()
                ) {
                    Spacer(GlanceModifier.height(5.dp))
                    GlanceText(
                        text = currentlyPlaying ?: "No title"
                    )
                    GlanceText(
                        text = currentArtist ?: "No artist",
                        color = ColorProvider(
                            GlanceTheme.colors.onBackground.getColor(context).copy(0.85f)
                        )
                    )
                    Row(
                        modifier = GlanceModifier.fillMaxSize(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircleIconButton(
                            imageProvider = ImageProvider(R.drawable.widget_previous),
                            contentDescription = null,
                            onClick = {
                                createWidgetPendingIntent(
                                    context,
                                    WIDGET_ACTION_SKIP_PREVIOUS
                                ).send()
                            },
                            modifier = GlanceModifier.size(45.dp)
                        )
                        if (isCurrentlyPlaying) {
                            CircleIconButton(
                                imageProvider = ImageProvider(R.drawable.widget_pause),
                                contentDescription = null,
                                onClick = {
                                    createWidgetPendingIntent(
                                        context,
                                        WIDGET_ACTION_PLAYORPAUSE
                                    ).send()
                                },
                                modifier = GlanceModifier.size(45.dp)
                            )
                        } else {
                            CircleIconButton(
                                imageProvider = ImageProvider(R.drawable.widget_play),
                                contentDescription = null,
                                onClick = {
                                    createWidgetPendingIntent(
                                        context,
                                        WIDGET_ACTION_PLAYORPAUSE
                                    ).send()
                                },
                                modifier = GlanceModifier.size(45.dp)
                            )
                        }
                        CircleIconButton(
                            imageProvider = ImageProvider(R.drawable.widget_next),
                            contentDescription = null,
                            onClick = {
                                createWidgetPendingIntent(
                                    context,
                                    WIDGET_ACTION_SKIP_NEXT
                                ).send()
                            },
                            modifier = GlanceModifier.size(45.dp)
                        )
                    }
                }

            }

        }
    }

    override fun onCompositionError(
        context: Context,
        glanceId: GlanceId,
        appWidgetId: Int,
        throwable: Throwable
    ) {
        super.onCompositionError(context, glanceId, appWidgetId, throwable)
        println("Widget error: $throwable.")
    }
}

class MusicWidgetReceiver : GlanceAppWidgetReceiver() {

    private val ioScope = CoroutineScope(Dispatchers.IO)
    override val glanceAppWidget: GlanceAppWidget = MusicWidget

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        // Don't update metadata if UPDATE_APP is sent from the system
        val action = intent.getStringExtra(WIDGET_NEW_DATA) ?: return

        ioScope.launch {
            GlanceAppWidgetManager(context)
                .getGlanceIds(MusicWidget.javaClass)
                .forEach { glanceId ->

                    if (action == WIDGET_NEW_IS_PLAYING) {
                        updateAppWidgetState(context, glanceId) { prefs ->
                            prefs[IS_CURRENTLY_PLAYING_WIDGET] =
                                intent.getBooleanExtra("isPlaying", false)
                        }
                    } else {
                        updateAppWidgetState(context, glanceId) { prefs ->
                            prefs[CURRENTLY_PLAYING_WIDGET] =
                                intent.getStringExtra("title") ?: "<unknown>"
                            prefs[CURRENT_ARTIST_WIDGET] =
                                intent.getStringExtra("artist") ?: "<unknown>"
                            prefs[CURRENT_ART_URI_WIDGET] = intent.getStringExtra("artUri") ?: ""
                        }
                    }
                    glanceAppWidget.update(context, glanceId)
                }

        }


    }

}

@Composable
private fun GlanceText(
    text: String,
    modifier: GlanceModifier = GlanceModifier,
    color: ColorProvider = GlanceTheme.colors.onBackground,
) {

    Text(
        text = text,
        modifier = modifier,
        style = TextStyle(
            color = color,
        )
    )
}