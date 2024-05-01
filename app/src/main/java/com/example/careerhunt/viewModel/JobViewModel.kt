package com.example.careerhunt.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.example.careerhunt.AddJob
import com.example.careerhunt.dao.JobDAO
import com.example.careerhunt.data.Company
import com.example.careerhunt.data.Job
import com.example.careerhunt.database.CareerHuntDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JobViewModel (application: Application): AndroidViewModel(application){
    private val readRecommendedJob: LiveData<List<Job>>
    private val readNewJob: LiveData<List<Job>>

    //Repository
    private val repository: JobRepository
    init {
        val jobDAO = CareerHuntDatabase.getDatabase(application).jobDAO()
        repository = JobRepository(jobDAO)

        readRecommendedJob = repository.readRecommededJob
        readNewJob = repository.readNewJob
    }

    //Insert
    fun addJob(job: Job){
        viewModelScope.launch (Dispatchers.IO){
            repository.addJob(job)
        }
    }

    //Read all - without passing parameter
    fun readRecommendedJob() : LiveData<List<Job>> {
        return readRecommendedJob
    }

    //Read all - without passing parameter
    fun readNewJob() : LiveData<List<Job>> {
        return readNewJob
    }

    //Get Job By Passing ID
    fun readJob(jobID: Int): LiveData<Job> {
        return repository.readJob(jobID)
    }


    //RecyclerViewInterface -  Allow for clicking on each item inside recyclerView
    interface RecyclerViewEvent {
        fun onItemClick(position: Int);
    }

}