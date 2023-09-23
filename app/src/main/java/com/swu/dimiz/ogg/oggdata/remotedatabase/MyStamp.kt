package com.swu.dimiz.ogg.oggdata.remotedatabase

/*
fireStore 위치
User/사용자 이메일/Post/업로드 한 날짜
 */

class MyStamp ( //21일 스템프 기록
    var aim: Float = 0f,   //목표 감축량

    var dayNum: Int = 0,   //며칠째인지

    var dayCo2Sum: Float = 0f //해당 날짜에 줄인 총 량
)

