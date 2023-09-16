package com.swu.dimiz.ogg.oggdata.localdatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ActivitiesDailyDatabaseDao {

    @Insert
    fun insert(act: ActivitiesDaily)

    @Update
    fun update(act: ActivitiesDaily)

    @Query("SELECT * FROM daily_activities")
    fun getAllDailys() : LiveData<List<ActivitiesDaily>>

    @Query("SELECT * FROM daily_activities" + " ORDER BY dailyId DESC LIMIT 1")
    fun getDaily(): ActivitiesDaily?
}

@Dao
interface ActivitiesSustDatabaseDao {

    @Insert
    fun insert(act: ActivitiesSustainable)

    @Update
    fun update(act: ActivitiesSustainable)

    @Query("SELECT * FROM sustainable_activities")
    fun getAllSusts() : LiveData<List<ActivitiesSustainable>>
}

@Dao
interface ActivitiesExtraDatabaseDao {

    @Insert
    fun insert(act: ActivitiesExtra)

    @Update
    fun update(act: ActivitiesExtra)

    @Query("SELECT * FROM extra_activities")
    fun getAllExtras() : LiveData<List<ActivitiesExtra>>
}