package com.swu.dimiz.ogg.oggdata

//@Entity(tableName = "user_available_count")
data class MyLimit (

    //@Primary
    var limitId: Int = 0,

    var frequency: Int = 0,

    var limit: Int = 0,

    var postCount: Int = 0

        )