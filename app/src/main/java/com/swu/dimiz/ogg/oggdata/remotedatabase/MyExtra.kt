package com.swu.dimiz.ogg.oggdata.remotedatabase

/*
fireStore 위치
User/사용자 이메일/Extra/extraID

지속가능 활동 할때마다 추가됨
활동 계획 + 활동탭
 */
data class MyExtra(
    var extraID: Int = 0, //활동한 특별 활동이름    //필드 검색해서 존재하면 진행중으로 표시

    var strDay: Long? = null,    //등록한 날짜
)
