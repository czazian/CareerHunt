package com.example.careerhunt.viewModel

import androidx.lifecycle.LiveData
import com.example.careerhunt.dao.CompanyDAO
import com.example.careerhunt.data.Company
import com.example.careerhunt.data.Job

class CompanyRepository(private val companyDAO: CompanyDAO) {

    //Insert
    suspend fun addCompany(company: Company) {
        companyDAO.addCompany(company)
    }

    //Use the Foreign Key (from Job) to retrieve records (from Company)
    fun readCompany(companyID: Int): LiveData<Company> {
        return companyDAO.readCompany(companyID)
    }

}