package com.example.careerhunt.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Interview_FAQ",
    foreignKeys = [
        ForeignKey(
            entity = Interview_Category::class,
            parentColumns = ["faqCatID"],
            childColumns = ["faqCatID"],
        )
    ]
)
data class Interview_FAQ(
    @PrimaryKey(autoGenerate = true)
    var interFAQID:Int=0,
    @ColumnInfo(name = "overviewQuest")
    var overviewQuest: String,
    @ColumnInfo(name = "interviewQuest")
    var interviewQuest: String,
    @ColumnInfo(name = "faqCatID")
    var faqCatID: Int
)