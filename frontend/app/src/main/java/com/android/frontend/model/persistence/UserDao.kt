package com.android.frontend.model.persistence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query



@Dao
interface UserDao {
    @Query("SELECT refresh_token FROM user where uid=7")
    fun getRefreshToken(): String?

    @Insert
    fun insert(vararg users: User)

    @Query("UPDATE user SET refresh_token = :refreshToken where uid=7")
    fun update(refreshToken: String)

    @Delete
    fun delete(user: User)


    @Query("DELETE from user")
    fun deleteAll()
}