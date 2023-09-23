package com.swu.dimiz.ogg.oggdata.remotedatabase

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

/*
fireStore 위치
User/사용자 이메일/Post/업로드 한 날짜
 */

//@Entity(tableName = "user_post_list")
data class MyPost ( //하루에 포스트한 내용 (전체활동 올리면 여기로 저장)
    var postTime: Date?=null,  //올린 시간

    var actId: Int = 0,         //활동 ID

    var imageUrl: String = "",  //인증사진

    var reactionLike: Int = 0,
    var reactionFun: Int = 0,
    var reactionGreat: Int = 0,
    var reactionReport: Int = 0
)