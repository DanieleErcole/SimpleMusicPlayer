package com.example.musicplayer

import android.app.Application
import com.example.musicplayer.data.PlayerStateRepository
import com.example.musicplayer.services.MusicScanner
import com.example.musicplayer.data.UserPreferencesRepository
import com.example.musicplayer.di.AppContainer
import com.example.musicplayer.di.DefaultAppContainer
import com.example.musicplayer.services.player.PlayerController
import com.example.musicplayer.utils.dataStore

class MusicPlayerApplication() : Application() {

    lateinit var container: AppContainer
    lateinit var userPreferencesRepository: UserPreferencesRepository
    lateinit var playerStateRepository: PlayerStateRepository
    lateinit var scanner: MusicScanner
    lateinit var playerController: PlayerController

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        userPreferencesRepository = UserPreferencesRepository(dataStore)
        playerStateRepository = PlayerStateRepository(dataStore)
        scanner = MusicScanner(container.musicRepository)
        playerController = PlayerController(container.musicRepository, playerStateRepository)
    }

}