package it.danieleercole.soundbox.di

import it.danieleercole.soundbox.data.MusicRepository

interface AppContainer {
    val musicRepository: MusicRepository
}