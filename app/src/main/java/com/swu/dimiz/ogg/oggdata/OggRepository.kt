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

    // ──────────────────────────────────────────────────────────────────────────
    //                                   데일리
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateFreq(data: ActivitiesDaily) {
        database.dailyDatabaseDao.update(data)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateFreq(id: Int) {
        database.dailyDatabaseDao.updateFreqFromFirebase(id)
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

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun getTodayList(): LiveData<List<ActivitiesDaily>?> {
        return database.dailyDatabaseDao.getToday()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getFiltered(filter: String): List<ActivitiesDaily>? {
        return database.dailyDatabaseDao.getFilteredList(filter)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun dailyCo2(id: Int): Float {
        return database.dailyDatabaseDao.getCo2(id)
    }

    // ──────────────────────────────────────────────────────────────────────────
    //                                  지속가능
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun sustCo2(id: Int): Float {
        return database.sustDatabaseDao.getCo2(id)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateSustDate(id: Int, date: Long) {
        database.sustDatabaseDao.updateSustDateFromFirebase(id, date)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getSustDate(id: Int): ActivitiesSustainable {
        return database.sustDatabaseDao.getSust(id)
    }

    // ──────────────────────────────────────────────────────────────────────────
    //                                    특별
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateExtraDate(id: Int, date: Long) {
        database.extraDatabaseDao.updateExtraDateFromFirebase(id, date)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getExtraDate(id: Int): ActivitiesExtra {
        return database.extraDatabaseDao.getItem(id)
    }

    // ──────────────────────────────────────────────────────────────────────────
    //                                    배지
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getFilter(): List<String>? {
        return database.badgesDatabaseDao.getFilter()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getFilterDate(filter: String): List<Long>? {
        return database.badgesDatabaseDao.getFilteredList(filter)
    }

    fun getInventory(): LiveData<List<Badges>> {
        return database.badgesDatabaseDao.getBadgeInventory()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getBadge(id: Int): Badges {
        return database.badgesDatabaseDao.getItem(id)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateBadge(id: Int, date: Long, count: Int) {
        database.badgesDatabaseDao.updateBadgeFromFirebase(id, date, count)
    }

    // ──────────────────────────────────────────────────────────────────────────
    //                                 데일리 설명
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun getInstructions(id: Int, limit: Int): LiveData<List<Instruction>> {
        return database.instructionDatabaseDao.getDirections(id, limit)
    }
}