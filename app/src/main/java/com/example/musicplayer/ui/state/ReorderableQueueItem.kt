package com.example.musicplayer.ui.state

import com.example.musicplayer.data.QueuedTrack

class ReorderableQueueItem(val track: QueuedTrack) {

    var position = track.queuedItem.position

    fun move(to: Int) {
        position = to
    }

}