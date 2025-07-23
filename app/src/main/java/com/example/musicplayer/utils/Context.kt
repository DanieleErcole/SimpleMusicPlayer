package com.example.musicplayer.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

fun Context.hasPermission(permission: String) = ContextCompat.checkSelfPermission(
    applicationContext,
    permission
) == PackageManager.PERMISSION_GRANTED

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = SCANNED_DIRS_PREFERENCE_NAME
)