package com.example.musicplayer.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.musicplayer.data.Playlist
import com.example.musicplayer.data.TrackWithAlbum
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Transaction
    @Query("SELECT * FROM playlist ORDER BY created ASC")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Transaction
    @Query("""
        SELECT t.* FROM TrackWithAlbum t 
        JOIN trackAddedTOPlaylist tAdded ON tAdded.trackId = t.trackId 
        WHERE tAdded.playlistId = :id 
            AND (:searchString IS NULL OR t.title LIKE '%' || :searchString || '%' OR t.name LIKE '%' || :searchString || '%')
    """)
    fun getPlaylistTracks(id: Long, searchString: String?): Flow<List<TrackWithAlbum>>

    @Transaction
    @Query("SELECT p.* FROM playlist p JOIN trackAddedTOPlaylist t ON t.playlistId = p.playlistId WHERE t.trackId = :id")
    fun getTrackPlaylists(id: Long): Flow<List<Playlist>>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(pl: Playlist)

    @Transaction
    @Delete
    suspend fun delete(pl: Playlist)

}