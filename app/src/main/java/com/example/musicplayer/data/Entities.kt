package com.example.musicplayer.data

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.Instant

@Entity(tableName = "playlist")
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Long = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "created")
    val created: Instant
)

// I have to use the LEFT Join to also get the empty playlists
@DatabaseView(value = """
    SELECT p.*, GROUP_CONCAT(DISTINCT a.thumbnail) AS thumbnails FROM playlist p
    LEFT JOIN trackAddedToPlaylist tp ON tp.playlistId = p.playlistId
    LEFT JOIN track t ON t.trackId = tp.trackId
    LEFT JOIN album a ON a.id = t.album
    GROUP BY p.playlistId
    ORDER BY p.created ASC
""")
data class PlaylistWithThumbnails(
    @Embedded val playlist: Playlist,
    val thumbnails: String?
) {
    fun toThumbsList(): List<String> = thumbnails?.split(",") ?: emptyList()
}

@Entity(tableName = "album")
data class Album(
    @PrimaryKey()
    val id: Long = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "thumbnail")
    val thumbnail: String?,
    @ColumnInfo(name = "albumArtist")
    val artist : String = "Unknown"
)

@Entity(tableName = "track")
data class Track(
    @PrimaryKey()
    val trackId: Long = 0,
    @ColumnInfo(name = "location")
    val location: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "album")
    val album: Long,
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
    val addedToLibrary: Instant,
    @ColumnInfo(name = "lastPlayed")
    val lastPlayed: Instant? = null,
    @ColumnInfo(name = "playedCount")
    val playedCount: Int = 0
)

@DatabaseView(
    value = "SELECT t.*, a.* FROM track t JOIN album a ON t.album = a.id"
)
data class TrackWithAlbum(
    @Embedded val internal: Track,
    @Embedded val album: Album
)

@Entity(
    tableName = "trackAddedToPlaylist",
    primaryKeys = ["playlistId", "trackId"],
    foreignKeys = [
        ForeignKey(
            entity = Track::class,
            parentColumns = ["trackId"],
            childColumns = ["trackId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Playlist::class,
            parentColumns = ["playlistId"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TrackAddedToPlaylist(
    val playlistId: Long,
    val trackId: Long
)

@Entity(
    primaryKeys = ["trackId", "position"],
    tableName = "queue",
    foreignKeys = [
        ForeignKey(
            entity = Track::class,
            parentColumns = ["trackId"],
            childColumns = ["trackId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class QueueItem(
    @ColumnInfo(name = "trackId")
    val track: Long,
    @ColumnInfo(name = "position")
    val position: Int,
    @ColumnInfo(name = "isCurrent")
    val isCurrent: Boolean,
    @ColumnInfo(name = "lastPosition")
    val lastPosition: Long? // If position != null the track is the currently played one
)

data class QueuedTrack(
    @Embedded val queuedItem: QueueItem,
    @Relation(
        parentColumn = "trackId",
        entityColumn = "trackId",
    )
    val track: TrackWithAlbum
)