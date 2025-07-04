package com.example.musicplayer.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

// Directories to scan -> paths separated by $
class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private companion object {
        val SCANNED_DIRECTORIES = stringPreferencesKey("scannedDirectories")
        val FIRST_LAUNCH = booleanPreferencesKey("firstLaunch")
        const val TAG = "UserPreferencesRepo"
    }

    val scannedDirectories: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { prefs -> prefs[SCANNED_DIRECTORIES] ?: "" }
    val firstLaunch: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { prefs -> prefs[FIRST_LAUNCH] ?: false }

    suspend fun updateScannedDirs(search: String) = dataStore.edit { prefs -> prefs[SCANNED_DIRECTORIES] = search }
    suspend fun firstLaunched() = dataStore.edit { prefs -> prefs[FIRST_LAUNCH] = false }

}