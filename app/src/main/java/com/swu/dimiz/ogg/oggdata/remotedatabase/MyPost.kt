package com.swu.dimiz.ogg.oggdata.remotedatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_post_list")
data class MyPost (

    @PrimaryKey(autoGenerate = true)
    var postId: Long = 0L,

    val postTimeMilli: Long = System.currentTimeMillis(),

    var imageUrl: String = "",

    var reactionLike: Int = 0,
    var reactionFun: Int = 0,
    var reactionGreat: Int = 0,
    var reactionReport: Int = 0
        )