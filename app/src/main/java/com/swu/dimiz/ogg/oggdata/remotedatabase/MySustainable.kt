package com.swu.dimiz.ogg.oggdata.remotedatabase

import java.sql.Date

/*
fireStore 위치
User/사용자 이메일/Sustainable/sustID

지속가능 활동 할때마다 추가됨
활동탭
 */

data class  MySustainable(
    var sustID:Int?=null,      //활동한 지속가능한 활동이름    //필드 검색해서 존재하면 진행중으로 표시

    var strDay: Long?=null,    //등록한 날짜
    )
