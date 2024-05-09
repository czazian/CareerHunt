package com.example.careerhunt.data

import android.health.connect.datatypes.BloodPressureRecord.BloodPressureMeasurementLocation
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "Job", foreignKeys = [
    //Create Foreign Key Relationship
    ForeignKey(
        entity = Company::class,
        parentColumns = ["companyID"], //Reference to the Opposite Table Primary Key
        childColumns = ["companyID"], //Reference to the Own Table Foreign Key, to be Associated with the Opposite Table Primary Key.)
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )
])
//Specify the Own Table
data class Job(
    //Specify the Primary Key
    @PrimaryKey(autoGenerate = true)
    var jobID:Int = 0,

    //Specify the Columns
    @ColumnInfo(name = "JobName")
    var jobName: String,
    @ColumnInfo(name = "jobLocation")
    var jobLocation: String,
    @ColumnInfo(name = "jobCategory")
    var jobCategory: String,
    @ColumnInfo(name = "jobType")
    var jobType:String,
    @ColumnInfo(name = "jobSalary")
    var jobSalary: Double,
    @ColumnInfo(name = "jobDescription")
    var jobDescription: String,
    @ColumnInfo(name = "jobPostedDate")
    var jobPostedDate: String,

    //Specify the Foreign Key
    @ColumnInfo(name = "companyID")
    var companyID: Int
)

