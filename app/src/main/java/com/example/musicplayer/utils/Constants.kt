package com.example.musicplayer.utils

import android.net.Uri
import androidx.core.net.toUri
import com.example.musicplayer.data.Loop

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

object PlayerStateDefaults {
    const val VOLUME = 100f
    const val PAUSED = false
    val LOOP = Loop.None
}

object PlayerStateKeys {
    const val VOLUME = "volume"
    const val PAUSED = "paused"
    const val LOOP = "loop"
}

val albumUriBase: Uri = "content://media/external/audio/albumart".toUri()