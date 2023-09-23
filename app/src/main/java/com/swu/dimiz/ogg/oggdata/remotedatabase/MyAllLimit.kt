package com.swu.dimiz.ogg.oggdata.remotedatabase

/*
fireStore 위치
User/사용자 이메일/Activities/AllActivityInfo
 */

data class MyAllLimit (   //전체활동 정보
    var allId: Int = 0,     //전체활동 ID

    var allTitile: String = "",     //전체활동 제목

    var frequency: Int = 0,   //빈도 1 = 하루에 1번 , 2 = 하루에 몇번 (일반활동) , 3  = 며칠에 한번인지

    var limit: Int = 0,       //빈도 2일때 값이 있음 (0, 5, 10)
)