package com.example.musicplayer

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.musicplayer.services.MusicScanner
import com.example.musicplayer.data.UserPreferencesRepository
import com.example.musicplayer.di.AppContainer
import com.example.musicplayer.di.DefaultAppContainer

private const val SCANNED_DIRS_PREFERENCE_NAME = "scanned_dirs_prefs"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = SCANNED_DIRS_PREFERENCE_NAME
)

class MusicPlayerApplication() : Application() {

    lateinit var container: AppContainer
    lateinit var userPreferencesRepository: UserPreferencesRepository
    lateinit var scanner: MusicScanner

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        userPreferencesRepository = UserPreferencesRepository(dataStore)
        scanner = MusicScanner(container.musicRepository, userPreferencesRepository)
    }

}