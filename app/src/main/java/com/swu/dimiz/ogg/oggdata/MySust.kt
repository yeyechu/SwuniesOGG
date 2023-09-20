package com.swu.dimiz.ogg.oggdata

import android.graphics.Bitmap

//@Entity(tableName = "user_sustainable_count")
data class MySust (

    //@Primary
    var mySustId: Int = 0,

    var sustCode: Int = 0,

    var date: Long? = null,

    var image: Bitmap? = null,

    )