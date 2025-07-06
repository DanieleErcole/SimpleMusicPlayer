package com.example.musicplayer.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.LocalDateTime

@Entity(tableName = "playlist")
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "created")
    val created: LocalDateTime
)

@Entity(tableName = "album")
data class Album(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "thumbnail")
    val thumbnail: String?,
    @ColumnInfo(name = "artist")
    val artist : String = "Unknown"
)

@Entity(tableName = "track")
data class Track(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "location")
    val location: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "album")
    val album: Int? = null,
    @ColumnInfo(name = "artist")
    val artist: String = "Unknown",
    @ColumnInfo(name = "composer")
    val composer: String = "Unknown",
    @ColumnInfo(name = "genre")
    val genre: String = "Unknown",
    @ColumnInfo(name = "trackNumber")
    val trackNumber: Int = 1,
    @ColumnInfo(name = "discNumber")
    val discNumber: Int = 1,
    @ColumnInfo(name = "year")
    val year: Int,
    @ColumnInfo(name = "duration")
    val durationMs: Long,
    @ColumnInfo(name = "addedToLibrary")
    val addedToLibrary: LocalDateTime,
    @ColumnInfo(name = "lastPlayed")
    val lastPlayed: LocalDateTime? = null,
    @ColumnInfo(name = "playedCount")
    val playedCount: Int = 0
)

data class TrackWithAlbum(
    @Embedded val track: Track,
    @Relation(
        parentColumn = "album",
        entityColumn = "id"
    )
    val album: Album
)

data class AlbumWithTracks(
    @Embedded val album: Album,
    @Relation(
        parentColumn = "id",
        entityColumn = "album"
    )
    val tracks: List<Track>
)

@Entity(tableName = "trackAddedTOPlaylist", primaryKeys = ["playlistId", "trackId"])
data class TrackAddedToPlaylist(
    val playlistId: Int,
    val trackId: Int
)

data class PlaylistWithTracks(
    @Embedded val playlist: Playlist,
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "trackId",
        associateBy = Junction(TrackAddedToPlaylist::class)
    )
    val tracks: List<Track>
)

@Entity(primaryKeys = ["track", "added"], tableName = "queue")
data class QueueItem(
    @ColumnInfo(name = "trackId")
    val track: Int,
    @ColumnInfo(name = "added")
    val added: LocalDateTime,
    @ColumnInfo(name = "position")
    val position: Long? // If position != null the track is the currently played one
)

data class QueuedTrack(
    @Embedded val track: Track,
    @Relation(
        parentColumn = "id",
        entityColumn = "trackId",
    )
    val queuedItem: QueueItem
)