package com.example.careerhunt.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PersonalViewModal(application: Application) : AndroidViewModel(application) {

    private val repository : PersonalRepository

    init {
        val personalDAO = CareerHuntDatabase.getDatabse(application).personalDao()
        repository = PersonalRepository(personalDAO)
    }

    //fun addPersonal(personal: Personal){
    //    viewModelScope.launch (Dispatchers.IO ){
    //        repository.addPersonal(personal)
    //    }
    //}

   // fun getPersonalById(id : Int) : LiveData<Personal> {
   //     return repository.getPersonalById(id)
    //}
}