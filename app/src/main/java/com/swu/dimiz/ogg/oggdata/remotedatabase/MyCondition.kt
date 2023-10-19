package com.swu.dimiz.ogg.oggdata.remotedatabase

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

/*
fireStore 위치
User/사용자 이메일/
 */

//@Entity(tableName = "condition_record")
data class
MyCondition(             //사용자 기본 정보
    var email:String = "",            //사용자 기본 정보 자장

    var nickName: String = "",

    var aim: Float = 0f,            //목표 달성량

    var car: Int = 0,

    var startDate: Long = 0L,     //그래프 쪽에서 같은지 판별

    var report: Int = 0,            //신고 받은 횟수

    var projectCount: Int = 0,       //프로젝트 진행 횟수

    var extraPost: Int = 0           //특별활동 올린 횟수
)
