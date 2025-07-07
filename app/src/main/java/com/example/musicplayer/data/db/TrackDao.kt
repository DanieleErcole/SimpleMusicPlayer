package com.example.musicplayer.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.musicplayer.data.Album
import com.example.musicplayer.data.AlbumWithTracks
import com.example.musicplayer.data.Playlist
import com.example.musicplayer.data.PlaylistWithTracks
import com.example.musicplayer.data.Track
import com.example.musicplayer.data.TrackWithAlbum
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Query("SELECT * FROM track ORDER BY addedToLibrary ASC")
    suspend fun getAllTracks(): List<TrackWithAlbum>
    @Query("SELECT * FROM track ORDER BY addedToLibrary ASC")
    fun getAllTracksFlow(): Flow<List<TrackWithAlbum>>

    @Query("SELECT DISTINCT artist FROM track ORDER BY artist ASC")
    fun getAllArtists(): Flow<List<String>>

    @Query("SELECT * FROM track WHERE artist = :name ORDER BY addedToLibrary ASC")
    fun getArtistTracks(name: String): Flow<List<TrackWithAlbum>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(t: Track)

    @Delete
    suspend fun delete(t: Track)

}