package com.swu.dimiz.ogg.oggdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_post_list")
data class MyPost (

    @PrimaryKey(autoGenerate = true)
    var postId: Long = 0L,

    @ColumnInfo(name = "post_time_milli")
    val postTimeMilli: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "image_url")
    var imageUrl: String = "",

    @ColumnInfo(name = "reaction_like")
    var reactionLike: Int = 0,

    @ColumnInfo(name = "reaction_fun")
    var reactionFun: Int = 0,

    @ColumnInfo(name = "reaction_great")
    var reactionGreat: Int = 0,

    @ColumnInfo(name = "reaction_report")
    var reactionReport: Int = 0
        )