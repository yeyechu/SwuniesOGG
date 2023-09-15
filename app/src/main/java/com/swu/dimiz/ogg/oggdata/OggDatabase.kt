package com.swu.dimiz.ogg.oggdata

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.swu.dimiz.ogg.oggdata.internalDatabase.*

@Database(entities = [
    MyPost::class,
    ActivitiesDaily::class,
    ActivitiesSustainable::class,
    ActivitiesExtra::class], version = 1, exportSchema = false)
abstract class OggDatabase: RoomDatabase() {

    abstract val myPostDatabaseDao: MyPostDatabaseDao
    abstract val dailyDatabaseDao: ActivitiesDailyDatabaseDao
    abstract val sustDatabaseDao: ActivitiesSustDatabaseDao
    abstract val extraDatabaseDao: ActivitiesExtraDatabaseDao

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