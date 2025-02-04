package com.example.mobilecomputing.data

import androidx.room.*
import com.example.mobilecomputing.UserProfile

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE id = 0")
    suspend fun getProfile(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfile)
}