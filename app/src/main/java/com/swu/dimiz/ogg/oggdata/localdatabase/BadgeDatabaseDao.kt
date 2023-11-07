package com.swu.dimiz.ogg.oggdata.localdatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDatabaseDao {

    @Insert
    fun insert(data: Badges)

    // ───────────────────────────────────────────────────────────────────────
    //                                 업데이트
    @Update
    fun update(data: Badges)

    @Query("UPDATE badges SET getDate = :date, count = :count WHERE badgeId = :id")
    suspend fun updateBadgeFromFirebase(id: Int, date: Long, count: Int)

    @Query("UPDATE badges SET count = :count WHERE badgeId = :id")
    suspend fun updateBadgeCountFromFirebase(id: Int, count: Int)
    // ───────────────────────────────────────────────────────────────────────
    //                                라이브 데이터
    @Query("SELECT * FROM badges")
    fun getAllItem() : Flow<List<Badges>>

    @Query("SELECT * FROM badges WHERE getDate IS NOT NULL")
    fun getBadgeInventory() : LiveData<List<Badges>>

    // ───────────────────────────────────────────────────────────────────────
    //                                지연함수
    @Query("SELECT * FROM badges WHERE badgeId = :id")
    suspend fun getItem(id: Int) : Badges

    @Query("SELECT DISTINCT filter FROM badges")
    suspend fun getFilter(): List<String>?

    @Query("SELECT * FROM badges WHERE filter = :filter")
    suspend fun getFilteredBadgeList(filter: String): List<Badges>?
}