package com.swu.dimiz.ogg.oggdata.remotedatabase

/*
fireStore 위치
User/사용자 이메일/Project1/Entire/ALlAct/actID
*/

data class MyAllAct ( //전체활동 활동명 별 상황 저장 -> graph에서 사용
    var ID:Int = 0,

    var actCode:String = "",

    var upCount:Int =0,

    var allC02:Double =0.0,
)