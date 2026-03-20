package it.danieleercole.soundbox.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import it.danieleercole.soundbox.data.Album
import it.danieleercole.soundbox.data.TrackWithAlbum
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {

    @Query("SELECT * FROM album")
    suspend fun getAllAlbums(): List<Album>
    @Query("""
        SELECT * FROM album 
        WHERE :searchString IS NULL OR name LIKE '%' || :searchString || '%'
        ORDER BY name
    """)
    fun getAllAlbumsFlow(searchString: String?): Flow<List<Album>>

    @Query("""
        SELECT * FROM TrackWithAlbum 
        WHERE album = :id 
            AND (:searchString IS NULL OR title LIKE '%' || :searchString || '%' OR name LIKE '%' || :searchString || '%')
        ORDER BY discNumber, trackNumber
    """)
    fun getAlbumTracks(id: Long, searchString: String?): Flow<List<TrackWithAlbum>>
    @Query("SELECT COUNT(trackId) FROM TrackWithAlbum WHERE album = :id")
    suspend fun getAlbumTracksCount(id: Long): Int

    @Insert
    suspend fun insert(a: Album)

    @Delete
    suspend fun delete(a: Album)

}