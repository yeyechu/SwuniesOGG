package com.swu.dimiz.ogg.oggdata.remotedatabase

/*
fireStore 위치
User/사용자 이메일/Daily/upDay

활동 할때마다 날짜별로 추가됨
 */

data class MyDaily(
    var dailyID:Int?=null, //활동한 특별 활동이름    //필드 검색해서 존재하면 진행중으로 표시

    var upDate: Long?=null,    //올린 시각

    //몇번했는지

    var doLeft:Int = 0        //다시 할 수 횟수가 얼마나 남았는지   //할때마다 업데이트 됨
)
