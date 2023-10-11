package com.swu.dimiz.ogg.oggdata

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.swu.dimiz.ogg.oggdata.localdatabase.*
import kotlinx.coroutines.flow.Flow

class OggRepository(private val database: OggDatabase) {

    val getAlldata: Flow<List<ActivitiesDaily>> = database.dailyDatabaseDao.getAllDailys()
    val getAllSusts: Flow<List<ActivitiesSustainable>> = database.sustDatabaseDao.getAllSusts()
    val getAllextras: Flow<List<ActivitiesExtra>> = database.extraDatabaseDao.getAllExtras()
    val getAllBadges: Flow<List<Badges>> = database.badgesDatabaseDao.getAllItem()
    //val getAllInstructions: Flow<List<Instruction>> = database.instructionDatabaseDao.getAllDirections()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun getInstructions(id: Int, limit: Int): LiveData<List<Instruction>> {
        return database.instructionDatabaseDao.getDirections(id, limit)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getFiltered(filter: String): List<ActivitiesDaily>? {
        return database.dailyDatabaseDao.getFilteredList(filter)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun sustCo2(id: Int): Float {
        return database.sustDatabaseDao.getCo2(id)
    }


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getFilter(): List<String>? {
        return database.badgesDatabaseDao.getFilter()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getFilteredList(filter: String): List<Badges>? {
        return database.badgesDatabaseDao.getFilteredList(filter)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getFilteredListTitle(filter: String): List<String>? {
        return database.badgesDatabaseDao.getFilteredListTitle(filter)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateFreq(data: ActivitiesDaily) {
        database.dailyDatabaseDao.update(data)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateCar(data: Int) {
        database.dailyDatabaseDao.updateMobility(data)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun resetFreq() {
        database.dailyDatabaseDao.resetFreq()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun getActivity(id: Int): LiveData<ActivitiesDaily> {
        return database.dailyDatabaseDao.getItem(id)
    }

    fun getTodayList(): LiveData<List<ActivitiesDaily>> {
        return database.dailyDatabaseDao.getToday()
    }

    fun getInventory(): LiveData<List<Badges>> {
        return database.badgesDatabaseDao.getBadgeInventory()
    }
}