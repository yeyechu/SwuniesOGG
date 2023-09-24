package com.swu.dimiz.ogg.oggdata.remotedatabase

/*
fireStore 위치
기본 정보 이미 추가되어있음
App/Activity/
 */

data class AppActivityLimit (   //전체 활동 정보
    var ActivityId: Int = 0,             //전체활동 ID

    var ActivityTitile: String = "",     //전체활동 제목

    var ActivityCode: String = "",       //활동 코드

    var limit: Int = 0,                  //일반활동 하루에 몇번 가능한지 / 특별,지속 며칠에 한번 가능한지

    var ActivityCo2: Float = 0f,         //탄소 감축량
)