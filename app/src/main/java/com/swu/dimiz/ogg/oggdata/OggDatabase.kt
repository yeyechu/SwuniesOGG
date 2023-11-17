package com.swu.dimiz.ogg.oggdata

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.swu.dimiz.ogg.oggdata.localdatabase.*
import timber.log.Timber

@Database(
    entities = [
        ActivitiesDaily::class,
        ActivitiesSustainable::class,
        ActivitiesExtra::class,
        Badges::class,
        Instruction::class,
        UserInterface::class], version = 1, exportSchema = false
)
@TypeConverters(RoomTypeConverter::class)
abstract class OggDatabase : RoomDatabase() {

    abstract val dailyDatabaseDao: ActivitiesDailyDatabaseDao
    abstract val sustDatabaseDao: ActivitiesSustDatabaseDao
    abstract val extraDatabaseDao: ActivitiesExtraDatabaseDao
    abstract val badgesDatabaseDao: BadgeDatabaseDao
    abstract val instructionDatabaseDao : InstructionDatabaseDao
    abstract val uiDatabaseDao: UIDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: OggDatabase? = null

        fun getInstance(context: Context): OggDatabase {
            Timber.i("여기1")
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    Timber.i("여기2")
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        OggDatabase::class.java,
                        "ogg_database"
                    )
                        .addCallback(object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                Timber.i("여기3")
                                super.onCreate(db)
                            }
                        })
                        .fallbackToDestructiveMigration()
                        .createFromAsset("ogg_database.db")
                        .build()
                    Timber.i("여기4")
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}