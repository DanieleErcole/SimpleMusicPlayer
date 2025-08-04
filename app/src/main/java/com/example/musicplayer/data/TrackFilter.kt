package com.example.musicplayer.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

enum class ListMode {
    Tracks,
    Album,
    Playlist,
    Artist,
}

data class ListContext(
    val mode: ListMode,
    val id: Long? = null,
    val artist: String? = null
)

class TrackFilter(
    private val musicRepo: MusicRepository,
    private val ctx: ListContext,
    private val filters: List<String>? = null // Artists
) {

    fun collectTracks(search: String): Flow<List<TrackWithAlbum>> {
        return when (ctx.mode) {
            ListMode.Tracks -> musicRepo.getAllTracksFlow(filters, search.ifEmpty { null })
            ListMode.Album -> ctx.id?.let { musicRepo.getAlbumTracks(it, search.ifEmpty { null }) } ?: emptyFlow()
            ListMode.Playlist -> ctx.id?.let { musicRepo.getPlaylistTracks(it, search.ifEmpty { null }) } ?: emptyFlow()
            ListMode.Artist -> ctx.artist?.let { musicRepo.getArtistTracks(it, search.ifEmpty { null }) } ?: emptyFlow()
        }
    }

}