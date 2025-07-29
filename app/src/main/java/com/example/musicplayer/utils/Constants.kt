package com.example.musicplayer.utils

import android.net.Uri
import androidx.core.net.toUri

object DefaultAlbum {
    const val UNKNOWN_ID = -1L
    const val UNKNOWN = "Unknown"
    const val UNKNOWN_ALBUM_NAME = "$UNKNOWN Album"
    const val UNKNOWN_ARTIST = "Various Artists"
}

object UserPrefKeys {
    const val SCANNED_DIRS = "scannedDirectories"
    const val FIRST_LAUNCH = "firstLaunch"
    const val REPO_TAG = "UserPreferencesRepo"
}

object PlayerStateKeys {
    const val VOLUME = "volume"
    const val PAUSED = "paused"
    const val LOOP = "loop"
    const val SHUFFLE = "shuffle"
}

const val DEFAULT_VOLUME = 100f
const val THUMBNAILS_GRID_COUNT = 4
const val SCANNED_DIRS_PREFERENCE_NAME = "scanned_dirs_prefs"
val albumUriBase: Uri = "content://media/external/audio/albumart".toUri()