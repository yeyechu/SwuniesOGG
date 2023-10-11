package com.swu.dimiz.ogg.oggdata.localdatabase

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// ───────────────────────────────────────────────────────────────────────────────────────────────
//                                       매일 활동
@Entity(tableName = "daily")
data class ActivitiesDaily (

    @PrimaryKey
    var dailyId: Int = 0,

    var filter: String = "",

    var title: String = "",

    var co2: Float = 0f,

    var freq: Int = 0,

    var exclusive: Int = 1,

    var limit: Int = 0,

    var postCount: Int = 0,

    var instructionCount: Int = 0,

    var waytoPost: Int= 0,

    var image: Bitmap? = null,

    var guideImage: Bitmap? = null,

    var updateBadgeCode: Int = 0
)

// ───────────────────────────────────────────────────────────────────────────────────────────────
//                                      지속가능한 활동
@Entity(tableName = "sust")
data class ActivitiesSustainable (

    @PrimaryKey
    var sustId: Int = 0,

    var title: String = "",

    var filter: String = "",

    var co2: Float = 0f,

    var limit: Int = 0,

    var postCount: Int = 0,

    var instruction: String = "",

    var waytoPost: Int= 0,

    var image: Bitmap? = null,

    var guideImage: Bitmap? = null,

    var updateBadgeCode: Int = 0
)

// ───────────────────────────────────────────────────────────────────────────────────────────────
//                                       특별 활동
@Entity(tableName = "extra")
data class ActivitiesExtra (

    @PrimaryKey
    var extraId: Int = 0,

    var title: String = "",

    var filter: String = "",

    val co2: Float = 0f,

    var limit: Int = 0,

    var postCount: Int = 0,

    var instruction: String = "",

    var waytoPost: Int= 0,

    var image: Bitmap? = null,

    var guideImage: Bitmap? = null,

    var updateBadgeCode: Int = 0
)

