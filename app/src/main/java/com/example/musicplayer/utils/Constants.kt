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

val albumUriBase: Uri = "content://media/external/audio/albumart".toUri()