package com.example.careerhunt.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Blob

@Entity(tableName= "personal_table")
data class Personal(
    @PrimaryKey(autoGenerate = true)
    var personalID:Int=0,
    @ColumnInfo(name = "name")
    var name:String,
    @ColumnInfo(name = "email")
    var email:String,
    @ColumnInfo(name = "password")
    var password:String,
    @ColumnInfo(name = "graduated_from")
    var graduated_from:String,
    @ColumnInfo(name = "profile_img")
    var profile_img: ByteArray,
    @ColumnInfo(name = "phoneNum")
    var phoneNum:String,
    @ColumnInfo(name = "gender")
    var gender:String,
    @ColumnInfo(name = "jobField")
    var jobField:String
)

