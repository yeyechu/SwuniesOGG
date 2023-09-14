package com.swu.dimiz.ogg.oggdata

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MyPost::class], version = 1, exportSchema = false)
abstract class OggDatabase: RoomDatabase() {

    abstract val myPostDatabaseDao: MyPostDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: OggDatabase? = null

        fun getInstance(context: Context) : OggDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if(instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        OggDatabase::class.java,
                        "ogg_databases"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}