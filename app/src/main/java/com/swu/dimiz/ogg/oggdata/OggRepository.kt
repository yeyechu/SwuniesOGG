package com.swu.dimiz.ogg.oggdata

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.swu.dimiz.ogg.oggdata.localdatabase.*
import kotlinx.coroutines.flow.Flow

class OggRepository(private val database: OggDatabase) {

    val getAlldata : Flow<List<ActivitiesDaily>> = database.dailyDatabaseDao.getAllDailys()
    val getAllBadges : Flow<List<Badges>> = database.badgesDatabaseDao.getAllItem()
    val getAllSusts : Flow<List<ActivitiesSustainable>> = database.sustDatabaseDao.getAllSusts()
    val getAllextras : Flow<List<ActivitiesExtra>> = database.extraDatabaseDao.getAllExtras()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getFiltered(filter: String) : List<ActivitiesDaily>? {
        return database.dailyDatabaseDao.getFilteredList(filter)
    }

}