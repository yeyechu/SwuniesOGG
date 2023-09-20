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

    ///@Query("SELECT * FROM daily WHERE dailyId = :id")
    //fun getItem(id: Int) : ActivitiesDaily

    //@Query("SELECT * FROM daily WHERE filter = :data")
    //fun getFilteredList(data: String): LiveData<List<ActivitiesDaily>>
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

    //@Query("SELECT * FROM sust WHERE sustId = :id")
    //fun getItem(id: Int) : ActivitiesSust
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

    //@Query("SELECT * FROM extra WHERE extraId = :id")
    //fun getItem(id: Int) : ActivitiesExtra
}