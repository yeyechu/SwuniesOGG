package com.swu.dimiz.ogg.oggdata.localdatabase

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "badges")
data class Badges (

    @PrimaryKey
    var badgeId: Int = 0,

    var filter: String = "",

    var title: String = "",

    var instruction: String = "",

    var image: Bitmap? = null,

    var getDate: Long? = null,

    var count: Int = 0,

    var baseValue: Int = 0
        )