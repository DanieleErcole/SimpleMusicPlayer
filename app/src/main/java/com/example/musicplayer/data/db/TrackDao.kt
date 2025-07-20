package com.example.musicplayer.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.musicplayer.data.Track
import com.example.musicplayer.data.TrackWithAlbum
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Query("SELECT * FROM TrackWithAlbum ORDER BY addedToLibrary ASC")
    suspend fun getAllTracks(): List<TrackWithAlbum>
    @Query("""
        SELECT * FROM TrackWithAlbum 
        WHERE (:artists IS NULL OR artist in (:artists)) 
            AND (:searchString IS NULL OR title LIKE '%' || :searchString || '%' OR name LIKE '%' || :searchString || '%')
        ORDER BY addedToLibrary ASC
    """)
    fun getAllTracksFlow(artists: List<String>?, searchString: String?): Flow<List<TrackWithAlbum>>

    @Query("SELECT DISTINCT artist FROM track ORDER BY artist ASC")
    fun getAllArtists(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(t: Track)

    @Delete
    suspend fun delete(t: Track)
    @Query("DELETE FROM track WHERE trackId IN (:idList)")
    suspend fun deleteBlk(idList: List<Long>)

}