package com.swu.dimiz.ogg.oggdata.remotedatabase

import java.util.*


/*
fireStore 위치
Feed/사용자 이메일

전체 사용자 한꺼번에 저장되어있음
 */

data class Feed(
    var id:String?=null,

    var email: String ="",     //업로드한 사용자 (통계, 나의피드에서 이메일 검색으로 가져옴)

    var postTime: Date = Date(),  //올린 시간

    var actId: Int = 0,         //활동 ID

    var actCode: String = "",   //활동 코드

    var imageUrl: String = "",  //인증 사진

    var reactionLike: Int = 0,
    var reactionFun: Int = 0,
    var reactionGreat: Int = 0,
    var reactionReport: Int = 0
)