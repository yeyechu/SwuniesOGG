package com.swu.dimiz.ogg.oggdata.localdatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "list_set")
data class ListSet (

    @PrimaryKey(autoGenerate = true)
    var listId: Int = 1,

    @ColumnInfo(name = "activity_one")
    var activity1: Long = 0L,

    @ColumnInfo(name = "activity_two")
    var activity2: Long = 0L,

    @ColumnInfo(name = "activity_three")
    var activity3: Long = 0L,

    @ColumnInfo(name = "activity_four")
    var activity4: Long = 0L,

    @ColumnInfo(name = "activity_five")
    var activity5: Long = 0L
        )