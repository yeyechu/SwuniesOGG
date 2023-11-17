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
    suspend fun update(data: UserInterface)

    @Query("UPDATE user SET onboarding = 1 WHERE onboarding = 0")
    suspend fun upBoarding()

    @Query("SELECT onboarding FROM user")
    fun onBoarding(): Int
}