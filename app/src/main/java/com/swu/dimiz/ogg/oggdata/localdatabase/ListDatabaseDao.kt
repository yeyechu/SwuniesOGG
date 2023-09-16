package com.swu.dimiz.ogg.oggdata.localdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ListDatabaseDao {

    @Insert
    fun insert(data: ListSet)

    @Update
    fun update(data: ListSet)

    @Query("SELECT * FROM list_set ORDER BY listId DESC LIMIT :num")
    fun getLastItem(num: Int) : List<ListSet>

    @Query("SELECT * FROM list_set WHERE listId = :key")
    fun get(key: Long): ListSet

    @Query("DELETE FROM list_set")
    fun clear()
}