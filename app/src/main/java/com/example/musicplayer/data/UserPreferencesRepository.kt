package com.example.musicplayer.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.example.musicplayer.utils.UserPrefKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private companion object {
        val SCANNED_DIRECTORIES = stringSetPreferencesKey(UserPrefKeys.SCANNED_DIRS)
        val FIRST_LAUNCH = booleanPreferencesKey(UserPrefKeys.FIRST_LAUNCH)
    }

    val scannedDirectories: Flow<List<String>> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(UserPrefKeys.REPO_TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { prefs -> prefs[SCANNED_DIRECTORIES]?.toList() ?: emptyList() }
    val firstLaunch: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(UserPrefKeys.REPO_TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { prefs -> prefs[FIRST_LAUNCH] ?: true }

    suspend fun updateScannedDirs(list: List<String>) = dataStore.edit { prefs -> prefs[SCANNED_DIRECTORIES] = list.toSet() }
    suspend fun firstLaunched() = dataStore.edit { prefs -> prefs[FIRST_LAUNCH] = false }

}