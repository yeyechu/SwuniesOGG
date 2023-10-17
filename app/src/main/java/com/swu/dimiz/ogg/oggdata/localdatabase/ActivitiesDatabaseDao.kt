package com.swu.dimiz.ogg.oggdata.localdatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivitiesDailyDatabaseDao {

    @Insert
    fun insert(act: ActivitiesDaily)

    @Update
    suspend fun update(act: ActivitiesDaily)

    @Query("UPDATE daily SET exclusive = :data WHERE dailyId IN (10010, 10011)")
    suspend fun updateMobility(data: Int)

    @Query("UPDATE daily SET freq = 0")
    suspend fun resetFreq()

    @Query("UPDATE daily SET freq = 1 WHERE dailyId = :id")
    suspend fun updateFreqFromFirebase(id: Int)

    @Query("SELECT * FROM daily")
    fun getAllDailys() : Flow<List<ActivitiesDaily>>

    @Query("SELECT * FROM daily" + " ORDER BY dailyId DESC LIMIT 1")
    fun getDaily(): ActivitiesDaily?

    @Query("SELECT * FROM daily WHERE dailyId = :id")
    fun getItem(id: Int) : LiveData<ActivitiesDaily>

    @Query("SELECT * FROM daily WHERE filter = :data")
    suspend fun getFilteredList(data: String): List<ActivitiesDaily>?

    @Query("SELECT * FROM daily WHERE filter = :data")
    fun getFilteredLiveList(data: String): LiveData<List<ActivitiesDaily>>

    @Query("SELECT * FROM daily WHERE freq > 0 LIMIT 5")
    fun getToday(): LiveData<List<ActivitiesDaily>>

    @Query("SELECT dailyId FROM daily WHERE freq > 0 LIMIT 5")
    fun getTodayId(): LiveData<List<Int>>

    @Query("SELECT co2 FROM daily WHERE dailyId = :id")
    suspend fun getCo2(id: Int) : Float
}

@Dao
interface ActivitiesSustDatabaseDao {

    @Insert
    fun insert(act: ActivitiesSustainable)

    @Update
    fun update(act: ActivitiesSustainable)

    @Query("SELECT * FROM sust")
    fun getAllSusts() : Flow<List<ActivitiesSustainable>>

    @Query("SELECT * FROM sust WHERE sustId = :id")
    fun getItem(id: Int) : LiveData<ActivitiesSustainable>

    @Query("SELECT co2 FROM sust WHERE sustId = :id")
    suspend fun getCo2(id: Int) : Float
}

@Dao
interface ActivitiesExtraDatabaseDao {

    @Insert
    fun insert(act: ActivitiesExtra)

    @Update
    fun update(act: ActivitiesExtra)

    @Query("SELECT * FROM extra")
    fun getAllExtras() : Flow<List<ActivitiesExtra>>

    @Query("SELECT * FROM extra WHERE extraId = :id")
    fun getItem(id: Int) : LiveData<ActivitiesExtra>
}