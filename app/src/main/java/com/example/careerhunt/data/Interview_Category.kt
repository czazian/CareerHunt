package com.example.careerhunt.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Interview_Category")
data class Interview_Category(
    @PrimaryKey(autoGenerate = true)
    var faqCatID:Int=0,
    @ColumnInfo(name = "FAQcatName")
    var FAQcatName: String,
)