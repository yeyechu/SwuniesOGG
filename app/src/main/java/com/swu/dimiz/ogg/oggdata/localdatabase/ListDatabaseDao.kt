package com.swu.dimiz.ogg.oggdata.localdatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ListDatabaseDao {

    @Insert
    fun insert(data: ListSet)

    @Update
    fun update(data: ListSet)

    @Update
    fun updateLists(vararg lists: ListSet)

    // 활동 출력
    @Query("SELECT activity FROM list_set WHERE day = :today")
    fun getTodayList(today: Int) : LiveData<List<Int>>

    // 활동 수정 : 오늘 이후 남은 선택된 순번째의 activity 코드 리스트를 가져옴
    // suspend로 바꾸기
    @Query("SELECT activity FROM list_set WHERE day >= :today" + " AND ordering = :select")
    fun getLeftItem(today: Int, select: Int) : LiveData<List<Int>>

    // 활동 수정 : 오늘 리스트에서 해당 순번째 활동을 가져옴
    @Query("SELECT activity FROM list_set WHERE day = :today AND ordering = :select")
    fun getTodayItem(today: Int, select: Int): LiveData<List<Int>>

    // suspend로 바꾸기
    @Query("SELECT * FROM list_set")
    fun getAllItem() : Flow<List<ListSet>>

    // 테스트용으로 넣음
    @Query("SELECT * FROM list_set ORDER BY listId DESC LIMIT 1")
    fun getItem() : ListSet

    // 21일 끝난 후 전체 삭제
    @Query("DELETE FROM list_set")
    fun clear()
}