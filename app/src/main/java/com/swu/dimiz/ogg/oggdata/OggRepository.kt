package com.swu.dimiz.ogg.oggdata

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.swu.dimiz.ogg.oggdata.localdatabase.*
import kotlinx.coroutines.flow.Flow

class OggRepository(private val database: OggDatabase) {

    val getAlldata : Flow<List<ActivitiesDaily>> = database.dailyDatabaseDao.getAllDailys()
    val getAllSusts : Flow<List<ActivitiesSustainable>> = database.sustDatabaseDao.getAllSusts()
    val getAllextras : Flow<List<ActivitiesExtra>> = database.extraDatabaseDao.getAllExtras()
    val getAllBadges : Flow<List<Badges>> = database.badgesDatabaseDao.getAllItem()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun getInstructions(id: Int, limit: Int) : LiveData<List<Instruction>> {
        return database.instructionDatabaseDao.getDirections(id, limit)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getFiltered(filter: String) : List<ActivitiesDaily>? {
        return database.dailyDatabaseDao.getFilteredList(filter)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getFilter() : List<String>? {
        return database.badgesDatabaseDao.getFilter()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getFilteredList(filter: String) : List<Badges>? {
        return database.badgesDatabaseDao.getFilteredList(filter)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getFilteredListTitle(filter: String) : List<String>? {
        return database.badgesDatabaseDao.getFilteredListTitle(filter)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateFreq(data: ActivitiesDaily) {
        database.dailyDatabaseDao.update(data)
    }

    fun getInventory() : LiveData<List<Badges>> {
        return database.badgesDatabaseDao.getBadgeInventory()
    }
}