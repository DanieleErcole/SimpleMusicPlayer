package com.example.musicplayer.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.musicplayer.data.Playlist
import com.example.musicplayer.data.PlaylistWithThumbnails
import com.example.musicplayer.data.TrackAddedToPlaylist
import com.example.musicplayer.data.TrackWithAlbum
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Query("SELECT * FROM playlist ORDER BY created ASC")
    fun getAllPlaylists(): Flow<List<Playlist>>
    @Query("SELECT * FROM PlaylistWithThumbnails WHERE :searchString IS NULL OR name LIKE '%' || :searchString || '%'")
    fun getPlaylistsWithThumbnails(searchString: String?): Flow<List<PlaylistWithThumbnails>>

    @Query("""
        SELECT t.* FROM TrackWithAlbum t 
        JOIN trackAddedToPlaylist tAdded ON tAdded.trackId = t.trackId 
        WHERE tAdded.playlistId = :id 
            AND (:searchString IS NULL OR t.title LIKE '%' || :searchString || '%' OR t.name LIKE '%' || :searchString || '%')
    """)
    fun getPlaylistTracks(id: Long, searchString: String?): Flow<List<TrackWithAlbum>>

    // If a track is already on a playlist simply replace the id, that effectively does nothing
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToPlaylist(tracks: List<TrackAddedToPlaylist>)
    @Delete
    suspend fun removeFromPlaylist(tracks: List<TrackAddedToPlaylist>)

    @Query("UPDATE playlist SET name = :newName WHERE playlistId = :id")
    suspend fun rename(id: Long, newName: String)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(pl: Playlist)

    @Delete
    suspend fun delete(pl: Playlist)

}