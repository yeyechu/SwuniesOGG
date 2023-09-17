package com.swu.dimiz.ogg.oggdata.localdatabase

import androidx.lifecycle.LiveData
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


    // 오늘 이후 남은 전체 리스트를 가져옴
    // num은 21 - 오늘 + 1
    @Query("SELECT * FROM list_set ORDER BY listId DESC LIMIT :num")
    fun getLeftItem(num: Int) : LiveData<List<ListSet>>

    @Query("SELECT * FROM list_set")
    fun getAllItem() : LiveData<List<ListSet>>

    // 오늘 리스트만 가져옴
    @Query("SELECT * FROM list_set WHERE listId = :key")
    fun getTodayItem(key: Int): ListSet?

    // 테스트용으로 넣음
    @Query("SELECT * FROM list_set ORDER BY listId DESC LIMIT 1")
    fun getItem() : ListSet

    // 21일 끝난 후 전체 삭제
    @Query("DELETE FROM list_set")
    fun clear()
}