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
import com.example.musicplayer.utils.DEFAULT_VOLUME
import com.example.musicplayer.utils.PlayerStateKeys
import com.example.musicplayer.utils.UserPrefKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PlayerStateRepository(private val dataStore: DataStore<Preferences>) {

    private companion object {
        val VOLUME = floatPreferencesKey(PlayerStateKeys.VOLUME)
        val PAUSED = booleanPreferencesKey(PlayerStateKeys.PAUSED)
        val LOOP = stringPreferencesKey(PlayerStateKeys.LOOP)
        val SHUFFLE = booleanPreferencesKey(PlayerStateKeys.SHUFFLE)
    }

    private suspend fun FlowCollector<Preferences>.catch(ex: Throwable) {
        if (ex is IOException) {
            Log.e(UserPrefKeys.REPO_TAG, "Error reading player state.", ex)
            emit(emptyPreferences())
        } else {
            throw ex
        }
    }

    val paused: Flow<Boolean> = dataStore.data
        .catch { catch(it) }
        .map { prefs ->
            prefs[PAUSED] ?: false
        }
    val volume: Flow<Float> = dataStore.data
        .catch { catch(it) }
        .map { prefs ->
            prefs[VOLUME] ?: DEFAULT_VOLUME
        }
    val shuffle: Flow<Boolean> = dataStore.data
        .catch { catch(it) }
        .map { prefs ->
            prefs[SHUFFLE] ?: false
        }
    val loop: Flow<Loop> = dataStore.data
        .catch { catch(it) }
        .map { prefs ->
            Loop.valueOf(prefs[LOOP] ?: Loop.None.name)
        }

    suspend fun getPlayerState(): PlayerState {
        return dataStore.data.first().let { prefs ->
            PlayerState(
                volume = prefs[VOLUME] ?: DEFAULT_VOLUME,
                paused = prefs[PAUSED] ?: false,
                loopMode = Loop.valueOf(prefs[LOOP] ?: Loop.None.name),
                shuffle = prefs[SHUFFLE] ?: false
            )
        }
    }

    suspend fun updateVolume(vol: Float) = dataStore.edit { prefs -> prefs[VOLUME] = vol }
    suspend fun updatePaused(paused: Boolean) = dataStore.edit { prefs -> prefs[PAUSED] = paused }
    suspend fun updateLoop(mode: Loop) = dataStore.edit { prefs -> prefs[LOOP] = mode.name }
    suspend fun updateShuffle(value: Boolean) = dataStore.edit { prefs -> prefs[SHUFFLE] = value }

}