package com.example.careerhunt.data

import androidx.lifecycle.LiveData

class PersonalRepository(private val personalDAO : PersonalDAO)  {

    suspend fun addPersonal(personal: Personal){
        personalDAO.addPersonal(personal)
    }

    fun getPersonalById(id : Int) : LiveData<Personal> {
        return personalDAO.getPersonalById(id)
    }
}