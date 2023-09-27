package com.swu.dimiz.ogg.oggdata.remotedatabase

import java.sql.Date

/*
fireStore 위치
User/사용자 이메일/1(Profject1~..)/susID

지속가능 활동 할때마다 추가됨
활동탭
 */

data class MySustainable(
    var sustTitle:String ="",   //활동한 지속가능한 활동이름    //필드 검색해서 존재하면 진행중으로 표시

    var strDay: Date?=null,    //등록한 날짜

    var Limit: Int = 0,        //기본 제약 일 수

    var dayLeft:Int = 0        //다시 할 수 있는 날이 얼마나 남았는지 //0이되면 활동 삭제
    )
