package com.example.careerhunt.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PersonalDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPersonal(personal: Personal)

    @Query("SELECT * FROM personal WHERE personalID = :id LIMIT 1")
    fun getPersonalById(id: Int): LiveData<Personal>
}