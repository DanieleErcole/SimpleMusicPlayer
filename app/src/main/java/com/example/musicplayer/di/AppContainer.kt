package com.example.musicplayer.di

import com.example.musicplayer.data.MusicRepository

interface AppContainer {
    val musicRepository: MusicRepository
}