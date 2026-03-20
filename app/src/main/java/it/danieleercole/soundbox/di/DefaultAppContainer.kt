package it.danieleercole.soundbox.di

import android.content.Context
import it.danieleercole.soundbox.data.LocalMusicRepository
import it.danieleercole.soundbox.data.MusicRepository
import it.danieleercole.soundbox.data.db.AppDatabase

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val musicRepository: MusicRepository by lazy {
        val db = AppDatabase.getDatabase(context)
        LocalMusicRepository(db.trackDao(), db.playlistDao(), db.albumDao(), db.queueDao())
    }
}