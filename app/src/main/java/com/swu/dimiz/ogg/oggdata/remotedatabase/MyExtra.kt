package com.swu.dimiz.ogg.oggdata.remotedatabase

import java.sql.Date

/*
fireStore 위치
User/사용자 이메일/Extra/extraID

지속가능 활동 할때마다 추가됨
활동 계획 + 활동탭
 */
data class MyExtra(
    var extraID:Int?=null, //활동한 특별 활동이름    //필드 검색해서 존재하면 진행중으로 표시

    var strDay: Long?=null,    //등록한 날짜

    var Limit: Int = 0,        //기본 제약 일 수

    var dayLeft:Int = 0        //다시 할 수 있는 날이 얼마나 남았는지 //0이되면 활동 삭제 SnapshotListener사용
)
