package com.example.careerhunt.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.careerhunt.dao.CompanyDAO
import com.example.careerhunt.data.Company
import com.example.careerhunt.data.Job
import com.example.careerhunt.database.CareerHuntDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CompanyViewModel (application: Application): AndroidViewModel(application) {

    private val repository: CompanyRepository
    init {
        val companyDAO = CareerHuntDatabase.getDatabase(application).companyDAO()
        repository = CompanyRepository(companyDAO)
    }

    //Insert
    fun addCompany(company: Company){
        viewModelScope.launch (Dispatchers.IO){
            repository.addCompany(company)
        }
    }

    //Get specific Company By Passing ID - Use the Foreign Key (from Job) to retrieve records (from Company)
    fun readCompany(companyID: Int): LiveData<Company> {
        return repository.readCompany(companyID)
    }


}