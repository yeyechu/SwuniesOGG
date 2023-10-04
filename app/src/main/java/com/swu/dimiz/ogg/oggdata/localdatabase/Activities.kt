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

    @ColumnInfo(name = "daily_title")
    var title: String = "",

    @ColumnInfo(name = "daily_co2")
    var co2: Float = 0f,

    @ColumnInfo(name = "daily_freq")
    var freq: Int = 0,

    @ColumnInfo(name = "daily_limit")
    var limit: Int = 0,

    @ColumnInfo(name = "daily_instruction_count")
    var instructionCount: Int = 0,

    @ColumnInfo(name = "daily_ways_to_post")
    var waytoPost: Int= 0,

    @ColumnInfo(name = "daily_image")
    var image: Bitmap? = null,

    @ColumnInfo(name = "daily_guide_image")
    var guideImage: Bitmap? = null,

    @ColumnInfo(name = "update_badge_code")
    var updateBadgeCode: Int = 0
        )

// ───────────────────────────────────────────────────────────────────────────────────────────────
//                                      지속가능한 활동
@Entity(tableName = "sust")
data class ActivitiesSustainable (

    @PrimaryKey
    var sustId: Int = 0,

    @ColumnInfo(name = "sust_title")
    var title: String = "",

    @ColumnInfo(name = "sust_co2")
    var co2: Float = 0f,

    @ColumnInfo(name = "sust_limit")
    var limit: Int = 0,

    @ColumnInfo(name = "sust_instruction")
    var instructionCount: String = "",

    @ColumnInfo(name = "sust_ways_to_post")
    var waytoPost: Int= 0,

    @ColumnInfo(name = "sust_image")
    var image: Bitmap? = null,

    @ColumnInfo(name = "sust_guide_image")
    var guideImage: Bitmap? = null,

    @ColumnInfo(name = "update_badge_code")
    var updateBadgeCode: Int = 0
)

// ───────────────────────────────────────────────────────────────────────────────────────────────
//                                       특별 활동
@Entity(tableName = "extra")
data class ActivitiesExtra (

    @PrimaryKey
    var extraId: Int = 0,

    @ColumnInfo(name = "extra_title")
    var title: String = "",

    @ColumnInfo(name = "extra_co2")
    val co2: Float = 0f,

    @ColumnInfo(name = "extra_limit")
    var limit: Int = 0,

    @ColumnInfo(name = "extra_instruction")
    var instructionCount: String = "",

    @ColumnInfo(name = "extra_ways_to_post")
    var waytoPost: Int= 0,

    @ColumnInfo(name = "extra_image")
    var image: Bitmap? = null,

    @ColumnInfo(name = "extra_guide_image")
    var guideImage: Bitmap? = null,

    @ColumnInfo(name = "update_badge_code")
    var updateBadgeCode: Int = 0
)

