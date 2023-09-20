package com.swu.dimiz.ogg.oggdata.localdatabase

//@Entity(tableName = "activity_detail")
data class Instruction (

    //@PrimaryKey
    var insId: Int = 0,

    var activityId: Int = 0,

    var detail: String = ""

        )