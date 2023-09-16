package com.swu.dimiz.ogg.oggdata.localdatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// ───────────────────────────────────────────────────────────────────────────────────────────────
//                                       매일 활동
@Entity(tableName = "daily_activities")
data class ActivitiesDaily (

    @PrimaryKey
    var dailyId: Long = 0L,

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
    var waytoPost: Boolean = false,

    //@ColumnInfo(name = "daily_image", typeAffinity = ColumnInfo.BLOB)
    @ColumnInfo(name = "daily_image")
    var image: ByteArray? = null,

    @ColumnInfo(name = "daily_guide_image", typeAffinity = ColumnInfo.BLOB)
    var guideImage: ByteArray? = null
        ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ActivitiesDaily

        if (image != null) {
            if (other.image == null) return false
            if (!image.contentEquals(other.image)) return false
        } else if (other.image != null) return false
        if (guideImage != null) {
            if (other.guideImage == null) return false
            if (!guideImage.contentEquals(other.guideImage)) return false
        } else if (other.guideImage != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = image?.contentHashCode() ?: 0
        result = 31 * result + (guideImage?.contentHashCode() ?: 0)
        return result
    }
}

// ───────────────────────────────────────────────────────────────────────────────────────────────
//                                      지속가능한 활동
@Entity(tableName = "sustainable_activities")
data class ActivitiesSustainable (

    @PrimaryKey
    var sustId: Long = 0L,

    @ColumnInfo(name = "sust_title")
    var title: String = "",

    @ColumnInfo(name = "sust_co2")
    var co2: Float = 0f,

    @ColumnInfo(name = "sust_limit")
    var limit: Int = 0,

    @ColumnInfo(name = "sust_instruction")
    var instructionCount: String = "",

    @ColumnInfo(name = "sust_ways_to_post")
    var waytoPost: Boolean = false,

    @ColumnInfo(name = "sust_image", typeAffinity = ColumnInfo.BLOB)
    var image: ByteArray? = null,

    @ColumnInfo(name = "sust_guide_image", typeAffinity = ColumnInfo.BLOB)
    var guideImage: ByteArray? = null
)

// ───────────────────────────────────────────────────────────────────────────────────────────────
//                                       특별 활동
@Entity(tableName = "extra_activities")
data class ActivitiesExtra (

    @PrimaryKey
    var extraId: Long = 0L,

    @ColumnInfo(name = "extra_title")
    var title: String = "",

    @ColumnInfo(name = "extra_co2")
    val co2: Float = 0f,

    @ColumnInfo(name = "extra_limit")
    var limit: Int = 0,

    @ColumnInfo(name = "extra_instruction")
    var instructionCount: String = "",

    @ColumnInfo(name = "extra_ways_to_post")
    var waytoPost: Boolean = false,

    @ColumnInfo(name = "extra_image", typeAffinity = ColumnInfo.BLOB)
    var image: ByteArray?,

    @ColumnInfo(name = "extra_guide_image", typeAffinity = ColumnInfo.BLOB)
    var guideImage: ByteArray?
)

