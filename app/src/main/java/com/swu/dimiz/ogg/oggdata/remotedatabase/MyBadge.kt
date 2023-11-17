package com.swu.dimiz.ogg.oggdata.remotedatabase

/*
fireStore 위치
User/사용자 이메일/Bage/badgeID
*/


data class MyBadge(
    var badgeID:Int?=null,

    var getDate: Long?=null,   //획득 날짜

    var count: Int = 0,         //획득 조건

    var valueX : Double = 0.0,

    var valueY : Double = 0.0
)
