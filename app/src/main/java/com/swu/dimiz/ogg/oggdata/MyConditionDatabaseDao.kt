package com.swu.dimiz.ogg.oggdata

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyCondition

@Dao
interface MyConditionDatabaseDao {

    @Insert
    fun insert(data: MyCondition)

    @Update
    fun update(data: MyCondition)

    @Query("SELECT * FROM condition_record LIMIT 1")
    fun getData(data: MyCondition): MyCondition
}