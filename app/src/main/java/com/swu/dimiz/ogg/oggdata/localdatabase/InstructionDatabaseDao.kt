package com.swu.dimiz.ogg.oggdata.localdatabase

import androidx.lifecycle.LiveData

//@Dao
interface InstructionDatabaseDao {

    //@Insert
    fun insert(data: Instruction)

    //Update
    fun update(data: Instruction)

    //@Query("SELECT detail FROM activity_detail WHERE activityId = :id ORDER BY insId DESC LIMIT :valid")
    fun getDirections(id: Int, valid: Int): LiveData<List<String>>
}