package com.sosauce.cutemusic.utils

enum class AlbumSort {
    NAME,
    ARTIST
}

enum class TrackSort {
    TITLE,
    ARTIST,
    ALBUM,
    YEAR,
    DATE_MODIFIED,
    AS_ADDED // For playlist tracks ONLY
}

enum class ArtistSort {
    NAME,
    NB_TRACKS,
    NB_ALBUMS
}

enum class PlaylistSort {
    NAME,
    NB_TRACKS,
    TAGS,
    COLOR
}
