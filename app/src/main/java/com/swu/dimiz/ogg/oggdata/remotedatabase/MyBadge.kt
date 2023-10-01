package com.swu.dimiz.ogg.oggdata.remotedatabase

import java.sql.Date

/*
fireStore 위치
User/사용자 이메일/Bage/badgeID
*/


data class MyBadge(
    var badgeID:Int?=null,

    var getDate: Date?=null,   //획득 날짜

    var count: Int = 0         //획득 조건
)
