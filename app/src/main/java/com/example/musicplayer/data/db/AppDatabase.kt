package com.example.musicplayer.data.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.musicplayer.data.Album
import com.example.musicplayer.data.Playlist
import com.example.musicplayer.data.QueueItem
import com.example.musicplayer.data.Track
import com.example.musicplayer.data.TrackAddedToPlaylist
import com.example.musicplayer.data.TrackWithAlbum
import com.example.musicplayer.utils.InstantConverter

@Database(entities = [Track::class, Playlist::class, Album::class, TrackAddedToPlaylist::class, QueueItem::class], views = [TrackWithAlbum::class], version = 7)
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