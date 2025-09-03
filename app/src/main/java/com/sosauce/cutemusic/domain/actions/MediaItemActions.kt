package com.sosauce.cutemusic.data.actions

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest

sealed interface MediaItemActions {

    data class DeleteMediaItem(
        val uri: List<Uri>,
        val activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) : MediaItemActions

    data class ShareMediaItem(
        val uri: Uri,
    ) : MediaItemActions

}