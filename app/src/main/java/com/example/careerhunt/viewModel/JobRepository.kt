package com.example.careerhunt.viewModel

import androidx.lifecycle.LiveData
import com.example.careerhunt.dao.JobDAO
import com.example.careerhunt.data.Company
import com.example.careerhunt.data.Job

class JobRepository (private val jobDAO: JobDAO) {
    //Select
    val readRecommededJob : LiveData<List<Job>> = jobDAO.readRecommendedJob()
    val readNewJob : LiveData<List<Job>> = jobDAO.readNewJob()

    //Select a JOB with passing value
    fun readJob(jobID: Int): LiveData<Job>{
        return jobDAO.readJob(jobID)
    }

    //Insert
    suspend fun addJob(job: Job) {
        jobDAO.addJob(job)
    }
}