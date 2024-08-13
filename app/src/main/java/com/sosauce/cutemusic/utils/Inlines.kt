package com.sosauce.cutemusic.utils

import android.os.Build

inline fun isOnAndroid11OrUp() : Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R