package com.swu.dimiz.ogg.oggdata

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MyPost::class], version = 1, exportSchema = false)
abstract class OggDatabase: RoomDatabase() {
}