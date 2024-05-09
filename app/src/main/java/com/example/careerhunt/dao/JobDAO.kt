package com.example.careerhunt.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.careerhunt.data.Company
import com.example.careerhunt.data.Job

@Dao
interface JobDAO {

    //Read Top 5 Recommended Job
    @Query("SELECT * FROM Job j, company_table c WHERE j.companyID = c.companyID ORDER BY jobPostedDate DESC LIMIT 5")
    fun readRecommendedJob(): LiveData<List<Job>>

    //Read Top 5 new posted Job
    @Query("SELECT * FROM Job j, company_table c ORDER BY jobPostedDate DESC LIMIT 5")
    fun readNewJob(): LiveData<List<Job>>

    //Insert into the database
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addJob(job: Job)


    //Get records by passing jobID
    @Query("SELECT * FROM Job WHERE jobID = :jobID")
    fun readJob(jobID: Int): LiveData<Job>
}