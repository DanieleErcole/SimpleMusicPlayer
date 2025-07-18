package com.example.musicplayer.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.musicplayer.utils.PlayerStateDefaults
import com.example.musicplayer.utils.PlayerStateKeys
import com.example.musicplayer.utils.UserPrefKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PlayerStateRepository(private val dataStore: DataStore<Preferences>) {

    private companion object {
        val VOLUME = floatPreferencesKey(PlayerStateKeys.VOLUME)
        val PAUSED = booleanPreferencesKey(PlayerStateKeys.PAUSED)
        val LOOP = stringPreferencesKey(PlayerStateKeys.LOOP)
    }

    val playerState: Flow<PlayerState> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(UserPrefKeys.REPO_TAG, "Error reading player state.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { prefs ->
            PlayerState(
                volume = prefs[VOLUME] ?: PlayerStateDefaults.VOLUME,
                paused = prefs[PAUSED] ?: PlayerStateDefaults.PAUSED,
                loopMode = Loop.valueOf(prefs[LOOP] ?: PlayerStateDefaults.LOOP.name)
            )
        }

    suspend fun getPlayerState(): PlayerState {
        return dataStore.data.first().let { prefs ->
            PlayerState(
                volume = prefs[VOLUME] ?: PlayerStateDefaults.VOLUME,
                paused = prefs[PAUSED] ?: PlayerStateDefaults.PAUSED,
                loopMode = Loop.valueOf(prefs[LOOP] ?: PlayerStateDefaults.LOOP.name)
            )
        }
    }

    suspend fun updateVolume(vol: Float) = dataStore.edit { prefs -> prefs[VOLUME] = vol }
    suspend fun updatePaused(paused: Boolean) = dataStore.edit { prefs -> prefs[PAUSED] = paused }
    suspend fun updateLoop(mode: Loop) = dataStore.edit { prefs -> prefs[LOOP] = mode.name }

}