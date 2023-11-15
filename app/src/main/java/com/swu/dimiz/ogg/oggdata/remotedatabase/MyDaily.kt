package com.swu.dimiz.ogg.oggdata.remotedatabase

import com.swu.dimiz.ogg.contents.listset.listutils.PostCount

/*
fireStore 위치
User/사용자 이메일/Project1/Daily/며칠자

활동 할때마다 날짜별로 추가됨
 */

data class MyDaily(
    var dailyID: Int? = null, //활동한 특별 활동이름    //필드 검색해서 존재하면 진행중으로 표시

    var upDate: Long? = null,    //올린 시각
    var postCount: Int = 0
)
