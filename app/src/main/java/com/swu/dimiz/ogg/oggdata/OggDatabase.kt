package com.swu.dimiz.ogg.oggdata

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.swu.dimiz.ogg.oggdata.localdatabase.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@Database(
    entities = [
        MyPost::class,
        ListSet::class,
        ActivitiesDaily::class,
        ActivitiesSustainable::class,
        ActivitiesExtra::class,
        Badges::class], version = 1, exportSchema = false
)
@TypeConverters(RoomTypeConverter::class)
abstract class OggDatabase : RoomDatabase() {

    abstract val myPostDatabaseDao: MyPostDatabaseDao
    abstract val listSetDatabaseDao: ListDatabaseDao
    abstract val dailyDatabaseDao: ActivitiesDailyDatabaseDao
    abstract val sustDatabaseDao: ActivitiesSustDatabaseDao
    abstract val extraDatabaseDao: ActivitiesExtraDatabaseDao
    abstract val badgesDatabaseDao: BadgeDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: OggDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope): OggDatabase {
            Timber.i("여기1")
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    Timber.i("여기2")
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        OggDatabase::class.java,
                        "ogg_databases"
                    )
                        .addCallback(object : RoomDatabase.Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                Timber.i("여기3")
                                super.onCreate(db)
                                internalDatabase(context.applicationContext)
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

        fun internalDatabase(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                //getInstance(context)!!.sustDatabaseDao.addData(S_DATA)
                //getInstance(context)!!.extraDatabaseDao.addData(E_DATA)
                Timber.i("여기5")
            }
        }
    }
}

private val S_DATA = arrayListOf(
    ActivitiesSustainable()
)
private val E_DATA = arrayListOf(
    ActivitiesExtra()
)