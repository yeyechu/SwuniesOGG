package com.swu.dimiz.ogg.oggdata

//@Dao
interface MyConditionDatabaseDao {

    //@Insert
    fun insert(data: MyCondition)

    //@Update
    fun update(data: MyCondition)

    //@Query("SELECT * FROM condition_record LIMIT 1")
    fun getData(data: MyCondition): MyCondition
}