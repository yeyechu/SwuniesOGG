package com.swu.dimiz.ogg.oggdata.remotedatabase

import androidx.room.PrimaryKey

/*
fireStore 위치
User/사용자 이메일/Activities/List
 */

//@Entity(tableName = "list_set")
data class MyList (  //21일간 활동 계획
    var day: Int = 0,
    var activity1: String?= null,
    var activity2: String?= null,
    var activity3: String?= null,
    var activity4: String?= null,
    var activity5: String?= null

//Limit체크 어떻게 할지
)

