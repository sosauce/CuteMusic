package com.sosauce.cutemusic.ui.customs

fun textCutter(text: String, cutAt: Int): String {
    return if (text.length < cutAt) {
        text
    } else {
        text.take(cutAt) + "..."
    }
}