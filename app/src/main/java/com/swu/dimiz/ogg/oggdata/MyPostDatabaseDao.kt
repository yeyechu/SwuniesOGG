package com.swu.dimiz.ogg.oggdata

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.swu.dimiz.ogg.oggdata.remotedatabase.MyPost

@Dao
interface MyPostDatabaseDao {

    // 인증하기 등록
    @Insert
    fun insert(post: MyPost)

    // 수정
    @Update
    fun update(post: MyPost)

    // post를 반환 -> 하나 or null
    @Query("SELECT * FROM user_post_list WHERE postId = :key")
    fun get(key: Long): MyPost

    // 전체 피드 가져오기
    @Query("SELECT * FROM user_post_list" + " ORDER BY postId DESC")
    fun getAllPosts() : LiveData<List<MyPost>>

    // ───────────────────────────────────────────────────────────────────────────────────────
    // 통계용
    // @Query("SELECT * FROM user_post_list ")

    @Query("SELECT * FROM user_post_list" + " ORDER BY postId DESC LIMIT 1")
    fun getPost(): MyPost?

    // 테이블의 전체 행을 삭제, 개발용으로만 사용 user 제공 기능 아님
    @Query("DELETE FROM user_post_list")
    fun clear()
}