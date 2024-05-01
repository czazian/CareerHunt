package com.example.careerhunt.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.careerhunt.data.Personal

@Dao
interface Apply_JobDAO {
    //For registration of Personal Account
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun registerAcc(personal: Personal)

    //Taking the value from the DB
    @Query("Select * from personal_table")
    fun retrievePersonalInfo(): LiveData<List<Personal>>
}