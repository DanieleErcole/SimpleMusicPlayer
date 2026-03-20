package it.danieleercole.soundbox

import android.app.Application
import it.danieleercole.soundbox.data.PlayerStateRepository
import it.danieleercole.soundbox.services.MusicScanner
import it.danieleercole.soundbox.data.UserPreferencesRepository
import it.danieleercole.soundbox.di.AppContainer
import it.danieleercole.soundbox.di.DefaultAppContainer
import it.danieleercole.soundbox.services.player.PlayerController
import it.danieleercole.soundbox.utils.dataStore

class MusicPlayerApplication : Application() {

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
        playerController = PlayerController(
            container.musicRepository,
            playerStateRepository,
            userPreferencesRepository
        )
    }

}