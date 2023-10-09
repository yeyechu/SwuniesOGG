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

    @Query("UPDATE daily SET daily_freq = 0")
    suspend fun resetFreq()

    @Query("SELECT * FROM daily")
    fun getAllDailys() : Flow<List<ActivitiesDaily>>

    @Query("SELECT * FROM daily" + " ORDER BY dailyId DESC LIMIT 1")
    fun getDaily(): ActivitiesDaily?

    @Query("SELECT * FROM daily WHERE dailyId = :id")
    fun getItem(id: Int) : LiveData<ActivitiesDaily>

    @Query("SELECT * FROM daily WHERE filter = :data")
    suspend fun getFilteredList(data: String): List<ActivitiesDaily>?

    @Query("SELECT * FROM daily WHERE daily_freq > 0 LIMIT 5")
    fun getToday(): LiveData<List<ActivitiesDaily>>
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