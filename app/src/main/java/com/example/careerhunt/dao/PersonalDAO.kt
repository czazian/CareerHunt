package com.example.careerhunt.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.careerhunt.data.Personal

@Dao
interface PersonalDAO {
    //For registration of Personal Account
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun registerAcc(personal: Personal)

    //Taking the value from the DB
    @Query("Select * from personal_table")
    fun retrievePersonalInfo(): LiveData<List<Personal>>

    // For forget password fragment to reset the password, but first the email must be matched only can reset
    @Update
    fun resetPasswd(vararg personal: Personal)

    // Based on the email, retrieve all personal details to be displayed in user profile
    @Query("Select * from personal_table where email = :email")// parameter passed in *Havent done
    // *****see want to add delete account button on profile or not

    // For editing profile
    @Update
    fun editPersonalProfile(vararg  personal: Personal)

    /*
    //:email --> use : to represent the parameter
    @Query("Delete from person_table where email = :email") // parameter passed in by user
    suspend fun delete(email:String) // ask user to pass in the email
    // cannot run directly under the main track, need to be back track (suspend)*/


}
