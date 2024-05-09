package com.example.careerhunt.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import java.sql.Blob

@Entity(
    tableName = "Apply_Job",
    primaryKeys = [ "jobID"],
    foreignKeys = [
        ForeignKey(
            entity = Job::class,
            parentColumns = ["jobID"],
            childColumns = ["jobID"],
        )
    ]
)
data class Apply_Job(
    @ColumnInfo(name = "jobID")
    var jobID: String,
    @ColumnInfo(name = "resume")
    var resume: ByteArray
)