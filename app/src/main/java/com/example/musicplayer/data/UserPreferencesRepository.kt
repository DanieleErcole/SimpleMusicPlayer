package com.example.musicplayer.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.musicplayer.utils.UserPrefKeys
import com.example.musicplayer.utils.catchError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private companion object {
        val AUTO_SCAN = booleanPreferencesKey(UserPrefKeys.AUTO_SCAN)
        val AUTO_PLAY = booleanPreferencesKey(UserPrefKeys.AUTO_PLAY)
    }

    val autoScan: Flow<Boolean> = dataStore.data
        .catch { catchError(it, UserPrefKeys.AUTO_SCAN) }
        .map { prefs ->
            prefs[AUTO_SCAN] ?: true
        }

    val autoPlay: Flow<Boolean> = dataStore.data
        .catch { catchError(it, UserPrefKeys.AUTO_PLAY) }
        .map { prefs ->
            prefs[AUTO_PLAY] ?: false
        }

    suspend fun isAutoPlay(): Boolean = autoPlay.first()

    suspend fun updateAutoScan(new: Boolean = true) {
        dataStore.edit { prefs -> prefs[AUTO_SCAN] = new }
    }

    suspend fun updateAutoPlay(new: Boolean = false) {
        dataStore.edit { prefs -> prefs[AUTO_PLAY] = new }
    }

}