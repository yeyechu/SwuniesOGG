package com.swu.dimiz.ogg.oggdata.remotedatabase

import androidx.room.PrimaryKey

/*
fireStore 위치
User/사용자 이메일/Activities1~../List

활동 계획 + 활동 탭
 */

//@Entity(tableName = "list_set")
data class MyList (  //21일간 활동 계획
    var day: Int = 0,
    var activity1: String?= null,
    var activity2: String?= null,
    var activity3: String?= null,
    var activity4: String?= null,
    var activity5: String?= null,

    var activityLimit1: Int?= null,
    var activityLimit2: Int?= null,
    var activityLimit3: Int?= null,
    var activityLimit4: Int?= null,
    var activityLimit5: Int?= null,

    var activityLeft1: Int?= null,
    var activityLeft2: Int?= null,
    var activityLeft3: Int?= null,
    var activityLeft4: Int?= null,
    var activityLeft5: Int?= null

)

