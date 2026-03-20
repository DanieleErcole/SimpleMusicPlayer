package it.danieleercole.soundbox.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import it.danieleercole.soundbox.data.Album
import it.danieleercole.soundbox.data.Playlist
import it.danieleercole.soundbox.data.PlaylistWithThumbnails
import it.danieleercole.soundbox.data.QueueItem
import it.danieleercole.soundbox.data.Track
import it.danieleercole.soundbox.data.TrackAddedToPlaylist
import it.danieleercole.soundbox.data.TrackWithAlbum
import it.danieleercole.soundbox.utils.InstantConverter

@Database(
    entities = [
        Track::class,
        Playlist::class,
        Album::class,
        TrackAddedToPlaylist::class,
        QueueItem::class
    ],
    views = [TrackWithAlbum::class, PlaylistWithThumbnails::class],
    version = 13)
@TypeConverters(InstantConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun albumDao(): AlbumDao
    abstract fun queueDao(): QueueDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
                    .fallbackToDestructiveMigration(false)
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}