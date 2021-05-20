package com.fortradestudio.mapowergeolocationtracker.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Query("Select * from user limit 1")
    fun getUserFromDatabase():User;

    @Insert
    fun insertToDatabase(user:User)

    @Query("Select Count(*) from user")
    fun getSize():Int
}