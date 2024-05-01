package com.example.careerhunt.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.careerhunt.data.Company

@Dao
interface CompanyDAO {

    //Insert into the database
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCompany(company: Company)


    //Get specific company details by passing company ID - Use the Foreign Key (from Job) to retrieve records (from Company)
    @Query("SELECT * FROM company_table WHERE companyID = :companyID")
    fun readCompany(companyID: Int): LiveData<Company>
}