package com.example.musicplayer.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.musicplayer.data.Track
import com.example.musicplayer.data.TrackWithAlbum
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Query("SELECT * FROM TrackWithAlbum ORDER BY addedToLibrary ASC")
    suspend fun getAllTracks(): List<TrackWithAlbum>
    @Query(
        """
        SELECT * FROM TrackWithAlbum 
        WHERE (:empty = 1 OR genre IN (:genres)) 
            AND (:searchString IS NULL OR title LIKE '%' || :searchString || '%' OR name LIKE '%' || :searchString || '%')
        ORDER BY title ASC
    """
    )
    fun getAllTracksFlow(genres: List<String>, empty: Boolean, searchString: String?): Flow<List<TrackWithAlbum>>

    @Query("""
        SELECT DISTINCT artist FROM track
        WHERE :searchString IS NULL OR artist LIKE '%' || :searchString || '%'
        ORDER BY artist ASC
    """)
    fun getAllArtists(searchString: String?): Flow<List<String>>

    @Query("""
        SELECT * FROM TrackWithAlbum
        WHERE artist = :artist
            AND (:searchString IS NULL OR title LIKE '%' || :searchString || '%' OR name LIKE '%' || :searchString || '%')
        ORDER BY title
    """)
    fun getArtistTracks(artist: String, searchString: String?): Flow<List<TrackWithAlbum>>

    @Query("SELECT DISTINCT genre FROM track ORDER BY genre ASC")
    fun getAllGenres(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(t: Track)

    @Delete
    suspend fun delete(t: Track)
    @Query("DELETE FROM track WHERE trackId IN (:idList)")
    suspend fun deleteBlk(idList: List<Long>)

}