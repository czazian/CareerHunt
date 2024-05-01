package com.example.careerhunt.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Answer_FAQ",
    foreignKeys = [
        ForeignKey(
            entity = Interview_FAQ::class,
            parentColumns = ["interFAQID"],
            childColumns = ["interFAQID"],
        )
    ]
)
data class Answer_FAQ(
    @PrimaryKey(autoGenerate = true)
    var faqAnsID:Int=0,
    @ColumnInfo(name = "answer")
    var answer: String,
    @ColumnInfo(name = "interFAQID")
    var interFAQID: String,
)