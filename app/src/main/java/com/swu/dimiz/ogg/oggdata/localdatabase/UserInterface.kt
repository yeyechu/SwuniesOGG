package com.swu.dimiz.ogg.oggdata.localdatabase

import androidx.room.*


@Entity(tableName = "user")
data class UserInterface (

    @PrimaryKey
    var onboarding: Int = 0

        )

@Dao
interface UIDatabaseDao {
    @Insert
    fun insert(data: UserInterface)

    @Update
    fun update(data: UserInterface)

    @Query("SELECT * FROM user")
    fun onBoarding(): Int
}