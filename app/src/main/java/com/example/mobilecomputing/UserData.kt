package com.example.mobilecomputing

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val id: Int = 0,
    val username: String,
    val imageUri: String?
)
