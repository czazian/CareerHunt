package com.example.careerhunt.data
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.careerhunt.Notification
import java.sql.Blob

@Entity(
    tableName = "Notification",
)
data class Notification(
    @PrimaryKey(autoGenerate = true)
    var notification:Int=0,
    @ColumnInfo(name = "notificationTitle")
    var notificationTitle: String,
    @ColumnInfo(name = "notificationContent")
    var notificationContent: String,

)