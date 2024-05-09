package com.example.careerhunt.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Blob

@Entity(tableName= "company_table")
data class Company(
    @PrimaryKey(autoGenerate = true)
    var companyID:Int=0,
    @ColumnInfo(name = "compEmail")
    var compEmail:String,
    @ColumnInfo(name = "compName")
    var compName:String,
    @ColumnInfo(name = "compPassword")
    var compPassword:String,
    @ColumnInfo(name = "compProfile")
    var compProfile: ByteArray,
    @ColumnInfo(name = "compPhoneNum")
    var compPhoneNum:String,
    @ColumnInfo(name = "compAddress")
    var compAddress:String
)