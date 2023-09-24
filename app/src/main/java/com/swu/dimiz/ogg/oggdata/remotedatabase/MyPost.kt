package com.swu.dimiz.ogg.oggdata.remotedatabase

import java.sql.Date

/*
fireStore 위치
Post/사용자 이메일

포스트는 전체 사용자 한꺼번에 저장함
 */

data class MyPost ( //하루에 포스트한 내용 (전체활동 올리면 여기로 저장)
    var postTime: Date?=null,  //올린 시간

    var actId: Int = 0,         //활동 ID

    var actCode: String = "",   //활동 코드

    var imageUrl: String = "",  //인증 사진

    var reactionLike: Int = 0,
    var reactionFun: Int = 0,
    var reactionGreat: Int = 0,
    var reactionReport: Int = 0
)