package com.example.musicplayer.utils

import androidx.room.TypeConverter
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class ZonedDateTimeConverter {

    @TypeConverter
    fun fromLongMillis(value: Long?): ZonedDateTime? {
        return value?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault())
        }
    }

    @TypeConverter
    fun fromDateTime(date: ZonedDateTime?): Long? {
        return date?.toInstant()?.toEpochMilli()
    }

}