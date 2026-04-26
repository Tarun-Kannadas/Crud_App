package com.example.mycrudapp.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.mycrudapp.entities.Users
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: Users)

    @Query("SELECT * FROM users WHERE username=:username LIMIT 1")
    suspend fun getUsersByUsername(username:String): Users?

    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAllUsers(): Flow<List<Users>>

    @Update
    fun updateUsers(users: Users)

    @Delete
    fun deleteUsers(users: Users)
}