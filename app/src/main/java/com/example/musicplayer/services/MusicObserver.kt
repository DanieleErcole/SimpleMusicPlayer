package com.example.musicplayer.services

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import com.example.musicplayer.data.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MusicObserver(
    private val scanner: MusicScanner,
    private val ctx: Context
) : ContentObserver(Handler(Looper.getMainLooper())) {

    private val scannerScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        scannerScope.launch {
            scanner.scanDirectories(ctx)
        }
    }

}