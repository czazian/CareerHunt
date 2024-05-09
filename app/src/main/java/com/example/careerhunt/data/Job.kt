package com.example.careerhunt.data

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class Job(
    var jobID: Long? = 0,
    val companyID: Int? = 0,
    var jobCategory: String? = "",
    var jobDescription: String? = "",
    var jobLocationState: String? = "",
    var jobLocationCity: String? = "",
    var jobName: String? = "",
    var jobPostedDate: String? = "",
    var jobSalary: Double? = 0.0,
    var jobType:String? = "",
) : Serializable
