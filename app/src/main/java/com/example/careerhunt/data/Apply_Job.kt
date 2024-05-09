package com.example.careerhunt.data

<<<<<<< HEAD
data class Apply_Job(
    var apply_jobID: Long? = 0,
    var jobID: Long? = 0,
    var personalID: Long? = 0
)
=======
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
>>>>>>> 762d4ad86aecfa2684b832ca2223ced83c5b0176
