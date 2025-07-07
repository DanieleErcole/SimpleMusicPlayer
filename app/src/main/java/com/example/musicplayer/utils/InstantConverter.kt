package com.example.musicplayer.utils

import androidx.room.TypeConverter
import java.time.Instant

class InstantConverter {

    @TypeConverter
    fun fromLongMillis(millis: Long?): Instant? {
        return millis?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun fromInstant(i: Instant?): Long? {
        return i?.toEpochMilli()
    }

}