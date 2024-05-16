package com.example.careerhunt.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.sql.Blob

data class Personal(
    var personalID :Int=0,
    var name:String="",
    var email: String="",
    var password:String="",
    var graduatedFrom:String="",
    var profileImg:String="",
    var phoneNum:String ="",
    var gender : String="",
    var jobField:String=""):Serializable

/*@Entity(tableName= "personal_table")
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
    var phoneNum:Int,
    @ColumnInfo(name = "gender")
    var gender:String,
    @ColumnInfo(name = "jobField")
    var jobField:String
)*/


