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

    @Insert
    suspend fun addData(acts: List<ActivitiesDaily>)

    @Update
    fun update(act: ActivitiesDaily)

    @Query("SELECT * FROM daily")
    fun getAllDailys() : Flow<List<ActivitiesDaily>>

    @Query("SELECT * FROM daily" + " ORDER BY dailyId DESC LIMIT 1")
    fun getDaily(): ActivitiesDaily?
}

@Dao
interface ActivitiesSustDatabaseDao {

    @Insert
    fun insert(act: ActivitiesSustainable)

    @Insert
    suspend fun addData(acts: List<ActivitiesSustainable>)

    @Update
    fun update(act: ActivitiesSustainable)

    @Query("SELECT * FROM sust")
    fun getAllSusts() : LiveData<List<ActivitiesSustainable>>
}

@Dao
interface ActivitiesExtraDatabaseDao {

    @Insert
    fun insert(act: ActivitiesExtra)

    @Insert
    suspend fun addData(acts: List<ActivitiesExtra>)

    @Update
    fun update(act: ActivitiesExtra)

    @Query("SELECT * FROM extra")
    fun getAllExtras() : LiveData<List<ActivitiesExtra>>
}