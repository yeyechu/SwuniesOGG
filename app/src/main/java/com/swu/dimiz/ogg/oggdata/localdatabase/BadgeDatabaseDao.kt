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

    @Update
    fun update(data: Badges)

    @Query("SELECT * FROM badges")
    fun getAllItem() : Flow<List<Badges>>

    @Query("SELECT * FROM badges WHERE badgeId = :id")
    suspend fun getItem(id: Int) : Badges

    @Query("SELECT * FROM badges WHERE filter = :data")
    suspend fun getFilteredList(data: String): List<Badges>?

    @Query("SELECT title FROM badges WHERE filter = :data")
    suspend fun getFilteredListTitle(data: String): List<String>?

    @Query("SELECT DISTINCT filter FROM badges")
    suspend fun getFilter(): List<String>?

    @Query("SELECT * FROM badges WHERE getDate IS NOT NULL")
    fun getBadgeInventory() : LiveData<List<Badges>>

}