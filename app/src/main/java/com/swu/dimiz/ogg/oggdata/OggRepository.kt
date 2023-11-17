package com.swu.dimiz.ogg.oggdata

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.swu.dimiz.ogg.oggdata.localdatabase.*
import kotlinx.coroutines.flow.Flow

class OggRepository(private val database: OggDatabase) {

    val getAllSusts: Flow<List<ActivitiesSustainable>> = database.sustDatabaseDao.getAllSusts()
    val getAllextras: Flow<List<ActivitiesExtra>> = database.extraDatabaseDao.getAllExtras()

    // ──────────────────────────────────────────────────────────────────────────
    //                                   데일리
    @WorkerThread
    suspend fun updateFreq(data: ActivitiesDaily) {
        database.dailyDatabaseDao.update(data)
    }

    @WorkerThread
    suspend fun updateFreq(id: Int) {
        database.dailyDatabaseDao.updateFreqFromFirebase(id)
    }

    @WorkerThread
    suspend fun updatePostCount(id: Int, data: Int) {
        database.dailyDatabaseDao.updatePostCountFromFirebase(id, data)
    }

    @WorkerThread
    suspend fun resetFreq() {
        database.dailyDatabaseDao.resetDaily()
    }

    @WorkerThread
    suspend fun getFiltered(filter: String): List<ActivitiesDaily>? {
        return database.dailyDatabaseDao.getFilteredList(filter)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun dailyCo2(id: Int): Float {
        return database.dailyDatabaseDao.getCo2(id)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun dailyCo2Sum(): Float? {
        return  database.dailyDatabaseDao.getCo2Sum()
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getTodayListInteger(): List<Int>? {
        return database.dailyDatabaseDao.getTodayIdCoroutines()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getActivityById(id: Int): ActivitiesDaily {
        return database.dailyDatabaseDao.getActivity(id)
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
    suspend fun getSust(id: Int): ActivitiesSustainable {
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
    suspend fun getFilteredBadgeList(filter: String): List<Badges>? {
        return database.badgesDatabaseDao.getFilteredBadgeList(filter)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateBadge(id: Int, date: Long, count: Int) {
        database.badgesDatabaseDao.updateBadgeFromFirebase(id, date, count)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateBadgeCount(id: Int, count: Int) {
        database.badgesDatabaseDao.updateBadgeCountFromFirebase(id, count)
    }

    // ──────────────────────────────────────────────────────────────────────────
    //                                 데일리 설명
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun getInstructions(id: Int, limit: Int): LiveData<List<Instruction>> {
        return database.instructionDatabaseDao.getDirections(id, limit)
    }
}