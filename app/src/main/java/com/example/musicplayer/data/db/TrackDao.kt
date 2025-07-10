package com.example.musicplayer.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.musicplayer.data.Album
import com.example.musicplayer.data.AlbumWithTracks
import com.example.musicplayer.data.Playlist
import com.example.musicplayer.data.PlaylistWithTracks
import com.example.musicplayer.data.Track
import com.example.musicplayer.data.TrackWithAlbum
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Transaction
    @Query("SELECT * FROM TrackWithAlbum ORDER BY addedToLibrary ASC")
    suspend fun getAllTracks(): List<TrackWithAlbum>
    @Transaction
    @Query("SELECT * FROM TrackWithAlbum ORDER BY addedToLibrary ASC")
    fun getAllTracksFlow(): Flow<List<TrackWithAlbum>>

    @Transaction
    @Query("SELECT DISTINCT artist FROM track ORDER BY artist ASC")
    fun getAllArtists(): Flow<List<String>>

    @Transaction
    @Query("SELECT * FROM TrackWithAlbum WHERE artist = :name ORDER BY addedToLibrary ASC")
    fun getArtistTracks(name: String): Flow<List<TrackWithAlbum>>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(t: Track)

    //TODO: make a bulk delete
    @Delete
    @Transaction
    suspend fun delete(t: Track)
    @Transaction
    @Query("DELETE FROM track WHERE trackId IN (:idList)")
    suspend fun deleteBlk(idList: List<Int>)

}