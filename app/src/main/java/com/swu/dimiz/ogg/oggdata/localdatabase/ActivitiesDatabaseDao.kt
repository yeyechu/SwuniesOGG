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

    // ───────────────────────────────────────────────────────────────────────
    //                                 업데이트
    @Update
    suspend fun update(act: ActivitiesDaily)

    @Query("UPDATE daily SET exclusive = :data WHERE dailyId IN (10010, 10011)")
    suspend fun updateMobility(data: Int)

    @Query("UPDATE daily SET freq = 0")
    suspend fun resetFreq()

    @Query("UPDATE daily SET freq = 1 WHERE dailyId = :id")
    suspend fun updateFreqFromFirebase(id: Int)

    // ───────────────────────────────────────────────────────────────────────
    //                                라이브 데이터
    @Query("SELECT * FROM daily")
    fun getAllDailys() : Flow<List<ActivitiesDaily>>

    @Query("SELECT * FROM daily WHERE dailyId = :id")
    fun getItem(id: Int) : LiveData<ActivitiesDaily>

    @Query("SELECT * FROM daily WHERE filter = :data")
    fun getFilteredLiveList(data: String): LiveData<List<ActivitiesDaily>>

    @Query("SELECT * FROM daily WHERE freq IN (1)")
    fun getToday(): LiveData<List<ActivitiesDaily>?>

    @Query("SELECT dailyId FROM daily WHERE freq IN (1)")
    fun getTodayId(): LiveData<List<Int>>

    // ───────────────────────────────────────────────────────────────────────
    //                                지연함수
    @Query("SELECT * FROM daily WHERE filter = :data")
    suspend fun getFilteredList(data: String): List<ActivitiesDaily>?

    @Query("SELECT co2 FROM daily WHERE dailyId = :id")
    suspend fun getCo2(id: Int) : Float
}

@Dao
interface ActivitiesSustDatabaseDao {

    @Insert
    fun insert(act: ActivitiesSustainable)

    // ───────────────────────────────────────────────────────────────────────
    //                                 업데이트
    @Update
    fun update(act: ActivitiesSustainable)

    @Query("UPDATE sust SET postDate = :date WHERE sustId = :id")
    suspend fun updateSustDateFromFirebase(id: Int, date: Long)

    // ───────────────────────────────────────────────────────────────────────
    //                                라이브 데이터
    @Query("SELECT * FROM sust")
    fun getAllSusts() : Flow<List<ActivitiesSustainable>>

    @Query("SELECT * FROM sust WHERE sustId = :id")
    fun getItem(id: Int) : LiveData<ActivitiesSustainable>

    // ───────────────────────────────────────────────────────────────────────
    //                                지연함수
    @Query("SELECT co2 FROM sust WHERE sustId = :id")
    suspend fun getCo2(id: Int) : Float

    @Query("SELECT * FROM sust WHERE sustId = :id")
    suspend fun getSust(id: Int) : ActivitiesSustainable
}

@Dao
interface ActivitiesExtraDatabaseDao {

    @Insert
    fun insert(act: ActivitiesExtra)

    // ───────────────────────────────────────────────────────────────────────
    //                                 업데이트
    @Update
    fun update(act: ActivitiesExtra)

    @Query("UPDATE extra SET postDate = :date WHERE extraId = :id")
    suspend fun updateExtraDateFromFirebase(id: Int, date: Long)

    // ───────────────────────────────────────────────────────────────────────
    //                                라이브 데이터
    @Query("SELECT * FROM extra")
    fun getAllExtras() : Flow<List<ActivitiesExtra>>

    // ───────────────────────────────────────────────────────────────────────
    //                                지연함수
    @Query("SELECT * FROM extra WHERE extraId = :id")
    suspend fun getItem(id: Int) : ActivitiesExtra
}