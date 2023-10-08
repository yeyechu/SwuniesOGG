package com.swu.dimiz.ogg.oggdata.localdatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface InstructionDatabaseDao {

    @Insert
    fun insert(data: Instruction)

    @Update
    fun update(data: Instruction)

    @Query("SELECT * FROM activity_detail")
    fun getAllDirections(): Flow<List<Instruction>>
    @Query("SELECT * FROM activity_detail WHERE activityId = :id ORDER BY insId DESC LIMIT :valid")
    fun getDirections(id: Int, valid: Int): LiveData<List<Instruction>>
}