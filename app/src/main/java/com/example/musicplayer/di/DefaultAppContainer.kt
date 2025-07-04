package com.example.musicplayer.di

import android.content.Context
import com.example.musicplayer.data.LocalMusicRepository
import com.example.musicplayer.data.MusicRepository
import com.example.musicplayer.data.db.AppDatabase

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val musicRepository: MusicRepository by lazy {
        val db = AppDatabase.getDatabase(context)
        LocalMusicRepository(db.trackDao(), db.playlistDao(), db.albumDao())
    }
}