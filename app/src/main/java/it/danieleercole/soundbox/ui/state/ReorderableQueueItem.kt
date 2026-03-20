package it.danieleercole.soundbox.ui.state

import it.danieleercole.soundbox.data.QueuedTrack

class ReorderableQueueItem(val track: QueuedTrack) {

    var position = track.queuedItem.position

    fun move(to: Int) {
        position = to
    }

}