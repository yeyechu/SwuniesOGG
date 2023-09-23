package com.swu.dimiz.ogg.oggdata.remotedatabase

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

/*
fireStore 위치
User/사용자 이메일/
 */

//@Entity(tableName = "condition_record")
data class MyCondition(       //사용자 기본 정보
    //@PrimaryKey
    var uid : String = "",  //firebase기본 제공 uid

    var email:String="",      //사용자 기본 정보 자장

    var password:String="",

    var nickName: String = "",   //닉네임 중복가능해서 primarykey에서 제외

    var aim: Float = 0f,         //목표 달성량

    var car: Int = 0,

    var startDate: Date?=null,   //그래프 쪽에서 같은지 판별

    var report: Int = 0          //신고 받은 횟수
)
