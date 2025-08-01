package com.example.musicplayer.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.musicplayer.utils.PlayerStateKeys
import com.example.musicplayer.utils.UserPrefKeys
import com.example.musicplayer.utils.catchError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private companion object {
        val AUTO_SCAN = booleanPreferencesKey(UserPrefKeys.AUTO_SCAN)
        val ACCENT_COLOR = intPreferencesKey(UserPrefKeys.ACCENT_COLOR)
    }

    val autoScan: Flow<Boolean> = dataStore.data
        .catch { catchError(it, PlayerStateKeys.PAUSED) }
        .map { prefs ->
            prefs[AUTO_SCAN] ?: true
        }

    val accentColor: Flow<Int> = dataStore.data
        .catch { catchError(it, PlayerStateKeys.PAUSED) }
        .map { prefs ->
            prefs[ACCENT_COLOR] ?: -1
        }

    suspend fun updateAutoScan(new: Boolean = true) {
        dataStore.edit { prefs -> prefs[AUTO_SCAN] = new }
    }

    suspend fun updateAccentColor(new: Int = -1) {
        dataStore.edit { prefs -> prefs[ACCENT_COLOR] = new }
    }

}