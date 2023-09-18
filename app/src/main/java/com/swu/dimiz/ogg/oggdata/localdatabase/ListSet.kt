package com.swu.dimiz.ogg.oggdata.localdatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "list_set")
data class ListSet (

    @PrimaryKey(autoGenerate = true)
    var listId: Int = 0,

    var day: Int = 0,
    var ordering: Int = 0,
    var activity: Int = 0
        )