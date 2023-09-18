package com.swu.dimiz.ogg.oggdata

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.swu.dimiz.ogg.oggdata.localdatabase.*
import kotlinx.coroutines.flow.Flow

class OggRepository(private val database: OggDatabase) {

    val getAlldata : Flow<List<ActivitiesDaily>> = database.dailyDatabaseDao.getAllDailys()


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(daily: ActivitiesDaily) {
        database.dailyDatabaseDao.insert(daily)
    }
}