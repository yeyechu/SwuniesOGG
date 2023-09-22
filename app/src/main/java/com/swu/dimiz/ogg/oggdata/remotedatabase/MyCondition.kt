package com.swu.dimiz.ogg.oggdata.remotedatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "condition_record")
data class MyCondition(

    @PrimaryKey
    var nickName: String = "",

    var aim: Float = 0f,

    var car: Int = 0,

    var startDate: Long = 0L,

    var report: Int = 0
)