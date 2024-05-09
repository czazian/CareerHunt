package com.example.careerhunt.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import java.sql.Blob

@Entity(
    tableName = "Apply_Job",
    primaryKeys = ["personalID", "jobID"],
    foreignKeys = [
        ForeignKey(
            entity = Personal::class,
            parentColumns = ["personalID"],
            childColumns = ["personalID"],
        ),
        ForeignKey(
            entity = Job::class,
            parentColumns = ["jobID"],
            childColumns = ["jobID"],
        )
    ]
)
data class Apply_Job(
    @ColumnInfo(name = "personalID")
    var personalID: String,
    @ColumnInfo(name = "jobID")
    var jobID: String,
    @ColumnInfo(name = "resume")
    var resume: ByteArray
)